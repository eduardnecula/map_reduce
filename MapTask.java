import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class MapTask extends Thread {
    private Task[] tasks; // task-urile pe care le voi prelucra
    private int nrThread; // nr de fire de executie
    private int idMap; // ca sa stiu care worker se ocupa de task-uri
    private int nrTaskuri;

    public MapTask(int idMap, Task[] tasks, int nrThread, int nrTaskuri) {
        this.idMap = idMap;
        this.tasks = tasks;
        this.nrThread = nrThread;
        this.nrTaskuri = nrTaskuri;
    }
/**
    Pentru a rula in paralel fiecare fir de executie
 */
    @Override
    public void run() {
        int start = (int) (idMap * (double)nrTaskuri / nrThread);
        int end = (int) Math.min((idMap + 1) * (double)nrTaskuri / nrThread, nrTaskuri);

        // fiecare fir de executie primeste o bucata din taskuri
        for (int i = start; i < end; i++) {
            String numeFisier = tasks[i].getNumeFisier();
            int idFisier = tasks[i].getIdTask();
            int dimensiuneFisier = tasks[i].getDimensiune();
            int offsetFisier = tasks[i].getOffset();

            try {
                setareCorecta(numeFisier, idFisier, dimensiuneFisier, offsetFisier);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // fiecare cuvant o sa vreau sa-l pun in hashMap
            setareMap(idFisier);
        }
    }

/**
    Dupa ce se setez corect cuvintele, voi pune intr-un map
    lungimea fiecarui cuvant, si nr de aparatii ale acestuia
 */
    public void setareMap(int idFis) {
        String fisier = tasks[idFis].getNewList();
        if (fisier != null) {
            int size = fisier.length();
            if (size != 0) {
                String[] words = fisier.split("\\W+");
                for (String word : words) {
                    ConcurrentHashMap<Integer, Integer> map =  tasks[idFis].getMap();
                    int dimWord = word.length();
                    if (word.length() > 0) {
                        if (map.containsKey(dimWord)) {
                            int lungime = map.get(dimWord);
                            lungime++;
                            if (lungime != 0) {
                                map.put(dimWord, lungime);
                            }
                        } else {
                            map.put(dimWord, 1);
                        }
                        tasks[idFis].setMap(map);
                    }
                }
            }
        }
}

/*
    In functie de pozitia cuvintelor pe care le am de exemplu,
    Task0: [ana a]
    Task1: [re me]
    Task2: [re]
    voi seta corect cuvintele: 
    [ana are]
    [mere]
    []
    Fiecare task este prelucrat individual, ori la stanga ori la dreapta
    Daca este prelucrat la dreapta, va cauta daca in urmatorul task incepe cu
    un caracter iar astfel il va lua si pe el: [ana a] va de deveni [ana are]

    Daca este prelucrat la stanga, verifica daca in spate se afla un caracter
    pe ultima pozitie, caz in care isi modifica offset-ul:
    [re me] devine [me], pentru ca re se poate lipi cu taskul din stanga lui

    Prelucrarea stanga si dreapta se fac pentru fiecare task in parte, mai putin
    primul task unde se prelucreaza doar la dreapta, iar ultimul task unde se
    prelucreaza doar la stanga
 */
private void setareCorecta(String numeFis, int idFis, int dimFisier, int offsetFis)
    throws FileNotFoundException {
        // deschid fisierul
        File file = new File(numeFis);
        Scanner scanner = new Scanner(file);
        scanner.useDelimiter(";:/?`.,><‘\\[]\\{}( )!@#$%^&-_+'=*\\/\n\t\r" +
                " ");

        // citesc continutul din fisier
        List<String> continutFisLista = new ArrayList<>();
        while (scanner.hasNext()) {
            continutFisLista.add(scanner.next());
        }
        String continutFisString  = continutFisLista.toString();    


        // ma pun exact pe offSet-ul din task-ul pe care il vreau
        int start = offsetFis + 1; // adaug 1 pentru ca sunt pe un array
        int end = start +  dimFisier;
        String doarLista = continutFisString.substring(start, end);

        // cand sunt pe primul task ma duc doar la dreapta
        if (idFis == 0) {
            // ma uit doar in dreapta
            int idDreapta = idFis + 1;
            String numeFisDr = tasks[idDreapta].getNumeFisier();
            int dimDr = tasks[idDreapta].getDimensiune();
            int offDr = tasks[idDreapta].getOffset() + 1;
            int endDr = offDr + dimDr;
            setareDreapta(continutFisString, doarLista, idDreapta, numeFisDr,
                    numeFis,
                    offDr,
                    endDr);
        } else if (idFis != (nrTaskuri - 1)) {
            // cand nu sunt pe ultimul task ma duc la stanga si la dreapta
            int idDreapta = idFis + 1;
            String numeFisDr = tasks[idDreapta].getNumeFisier();
            int dimDr = tasks[idDreapta].getDimensiune();
            int offDr = tasks[idDreapta].getOffset() + 1;
            int endDr = offDr + dimDr;
            setareDreapta(continutFisString, doarLista, idDreapta, numeFisDr,
                    numeFis,
                    offDr,
                    endDr);

            // ma duc pe stanga
            int idStanga = idFis - 1;
            String numeFisStg = tasks[idStanga].getNumeFisier();
            int dimStg = tasks[idStanga].getDimensiune();
            int startStg = tasks[idStanga].getOffset() + 1;
            int endStg = startStg + dimStg;
            setareStanga(continutFisString, doarLista, idStanga, numeFisStg,
                    numeFis, startStg,
                    endStg);
        } else {
            // ma uit doar in stanga
            int idStanga = idFis - 1;
            String numeFisStg = tasks[idStanga].getNumeFisier();
            int dimStg = tasks[idStanga].getDimensiune();
            int startStg = tasks[idStanga].getOffset() + 1;
            int endStg = startStg + dimStg;
            setareStanga(continutFisString, doarLista, idStanga, numeFisStg,
                    numeFis,
                    startStg,
                    endStg);
        }
        scanner.close();
    }

/*
    Prelucrare la dreapta [ana a] [re] devine [ana are]
 */
public void setareDreapta(String lista, String listaStanga, int id,
                              String nameFisDr,
                              String nameFis, int start,
                              int end) throws FileNotFoundException {
        // pot sa ma uit doar pe fisierul in care sunt curent
        if (nameFisDr.equals(nameFis)) {
            String listaDreapta = lista.substring(start, end);
            char[] listaCharDr = listaDreapta.toCharArray();
            int sizeDr = listaDreapta.length();
            String delimiters = ";:/?`.,><‘\\[]\\{}( )!@#$%^&-_+'=*\\/\n\t\r" +
                    " ";
            char[] delimChar = delimiters.toCharArray();
            int sizeDelim = delimChar.length;
            boolean gasit = false;
            int nrCar = 0;

            // ma uit pe lista din stanga, cea care m-a apelat
            // si vad daca se termina intr-un caracter spatiu
            char[] listaCharStanga = listaStanga.toCharArray();
            int sizeStg = listaCharStanga.length;
            if (listaCharStanga[sizeStg - 1] != ' ') {
                // caut in listaDreapta nr de litere si modific dimensiunea
                // task-ului curent
                for (int i = 0; i < sizeDr; i++) {
                    for (int j = 0; j < sizeDelim; j++) {
                        if (listaCharDr[i] == delimChar[j]) {
                            gasit = true;
                            break;
                        }
                    }
                    if (gasit) {
                        break;
                    }
                    nrCar++;
                }
            }

            // acum ca stiu nr de caractere
            // trebuie sa setez dimensiunea task-ului care m-a apelat
            int dimFisier = tasks[id - 1].getDimensiune();
            tasks[id - 1].setDimensiune(dimFisier + nrCar);
            tasks[id - 1].arataFisier();

        }
    }

/**
    Prelucrare la stanga: [re me] devine [me]
 */
    public void setareStanga(String lista, String listaDreapta,  int id,
                             String nameFisStg,
                             String nameFis,
                             int start, int end) throws FileNotFoundException {
        // pot sa ma uit doar pe fisierul in care sunt curent
        if (nameFis.equals(nameFisStg)) {
            String listaStanga = lista.substring(start, end);
            char[] listCharStg = listaStanga.toCharArray();
            int sizeStg = listCharStg.length;

            // ma uit pe lista din dreapta la final
            // si vad daca am un spatiu acolo
            char[] listCharDr = listaDreapta.toCharArray();
            int sizeDr = listCharDr.length;
            if (listCharStg[sizeStg - 1] != ' ') {
                // daca e diferit de spatiu
                // ma uit pe lista mea actuala, listaStanga, si nr cate
                // caractere am pana la un spatiu sau un alt caracter
                String delimiters = ";:/?`.,><‘\\[]\\{}( )!@#$%^&-_+'=*\\/\n\t\r" +
                        " ";
                char[] delimChar = delimiters.toCharArray();
                int sizeDelim = delimChar.length;
                boolean gasit = false;
                int nrCar = 0;

                for (int i = 0; i < sizeDr; i++) {
                    for (int j = 0; j < sizeDelim; j++) {
                        if (listCharDr[i] == delimChar[j]) {
                            gasit = true;
                            break;
                        }
                    }
                    if (gasit) {
                        break;
                    }
                    nrCar++;
                }

                // acum ca am numarat cate caractere am pana la spatiu
                // sau alt caracter
                // setez offset-ul task-ului curent, cat si dimensiunea
                int offSet = tasks[id + 1].getOffset();
                int dim = tasks[id + 1].getDimensiune();
                tasks[id + 1].setOffset(offSet + nrCar);
                tasks[id + 1].setDimensiune(dim - nrCar);
                tasks[id + 1].arataFisier();
            }
        }
    }
}
