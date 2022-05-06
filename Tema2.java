import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Tema2 {
    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("Usage: Tema2 <workers> <in_file> <out_file>");
            return;
        }
        
        // Clasa ReadInput se ocupa de citirea fisierului de intrare in care se va
        // da, nr de fire de executie, fisierul de intrare si cel de iesire
        ReadInput readInput = new ReadInput(args[0], args[1], args[2]);
        readInput.readInputFile();

        // Lista de fisire de intrare ex: test1.txt, test2.txt..
        ArrayList<String> listaFisiere = readInput.getInputFiles();
        int nrFisiere = listaFisiere.size();

        // ca sa stiu cate fisiere creez: ex 4 2 3
        ArrayList<Integer> listNrFis = new ArrayList<>();
        // ca sa stiu cate caractere are fiecare fisier ex 33, 10, 15
        ArrayList<Integer> listNrCuvinte = new ArrayList<>();
        // cate task-uri creez la final: ex: 9
        int nrTaskTotale = 0;
        for (int i = 0; i < nrFisiere; i++) {
            // deschid fiecare fisier si aflu cate task-uri pot sa pornesc
            // pentru fiecare
            int nrCaractere = readInput.cateTaskPornesc(listaFisiere.get(i));
            listNrCuvinte.add(nrCaractere);
            int fisDePornit = nrCaractere / readInput.getDimFragment();
            // daca am de ex 33, 33 % 10 o sa dea 3, care e mai mare decat 0
            // asta inseamna ca mai deschid inca un fisier (int)33 / 3 + 1 = 4
            if (fisDePornit % readInput.getDimFragment() > 0) {
                fisDePornit += 1;
            }
            listNrFis.add(fisDePornit);
            nrTaskTotale += fisDePornit;
        }

        // creez task-uri punand in ele, id-ul, fisierul din care face parte
        // si offset-ul initial
        Task[] tasks = new Task[nrTaskTotale];
        int contor = 0;
        int offset = 0;
        for (int j = 0; j < listaFisiere.size(); j++) {
            int cateFisiere = listNrFis.get(j);
            for (int i = 0; i < cateFisiere; i++) {
                // daca ajung la final pun cu modulo din cat ramane
                if (i == cateFisiere - 1) {
                    if (listNrFis.get(j) % readInput.getDimFragment() != 0) {
                        int offsetTask =
                                listNrCuvinte.get(j) % readInput.getDimFragment();
                        tasks[contor] = new Task(contor, listaFisiere.get(j),
                                offset,
                                offsetTask);
                    } else {
                        tasks[contor] = new Task(contor, listaFisiere.get(j),
                                offset,
                                readInput.getDimFragment());
                    }
                } else if (i == 0) {
                    tasks[contor] = new Task(contor, listaFisiere.get(j), 0,
                            readInput.getDimFragment());
                } else {
                    tasks[contor] = new Task(contor, listaFisiere.get(j), offset,
                            readInput.getDimFragment());
                }
                offset += readInput.getDimFragment();
                contor++;
            }
            offset = 0;
        }

        // aloc fiecare task workerilor
        MapTask[] mapTasks = new MapTask[readInput.getNrThread()];
        // pornesc thread-uri pentru cati workeri am
        for (int i = 0; i < readInput.getNrThread(); i++) {
            mapTasks[i] = new MapTask(i, tasks, readInput.getNrThread(),
                    nrTaskTotale);
            mapTasks[i].start();
        }

        for (int i = 0; i < readInput.getNrThread(); i++) {
            try {
                mapTasks[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // deschid alte threaduri pentru operatie de Reduce
        // sa zicem ca avem 10 fisiere
        // fiecare task reduce primeste 4 3 3 fisiere (sa zicem ca avem 3 fire de executie)
        Reduce[] reduces = new Reduce[readInput.getNrThread()];
        for (int i = 0; i < readInput.getNrThread(); i++) {
            reduces[i] = new Reduce(tasks, readInput.getInputFiles(), i,
                    readInput.getNrThread(), nrTaskTotale);
            reduces[i].start();
        }

        for (int i = 0; i < readInput.getNrThread(); i++) {
            try {
                reduces[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // acum tot ce trebuie sa fac este sa sortez in functie de rang
        // fisierele
        // voi face atatea obiecte, pentru cate fisiere am, pentru a sorta
        // fisierele dupa rang, folosind Comparable
        ArrayList<String> files = new ArrayList<>();
        ArrayList<String> rang = new ArrayList<>();
        ArrayList<Integer> lungime = new ArrayList<>();
        ArrayList<Integer> nrCuv = new ArrayList<>();

        for (int i = 0; i < readInput.getNrThread(); i++) {
            for (int j = 0; j < reduces[i].getListRang().size(); j++) {
                files.add(reduces[i].getNumeFisier().get(j));
                rang.add(reduces[i].getListRang().get(j));
                lungime.add(reduces[i].getMaxList().get(j));
                nrCuv.add(reduces[i].getNrMaxList().get(j));
            }
        }

        WriteOutput[] writeOutputs = new WriteOutput[files.size()];
        ArrayList<WriteOutput> list = new ArrayList<>();
        for (int i = 0; i < readInput.getNrFiles(); i++) {
            writeOutputs[i] = new WriteOutput(files.get(i),
                    rang.get(i),
                    lungime.get(i),
                    nrCuv.get(i));
            list.add(writeOutputs[i]);
        }

        // lista de obiecte de tip <nume_fis, rang, lungime, nrCuv>
        Collections.sort(list);
        FileWriter writer = new FileWriter(args[2]);

        // scriu in fisierul de iesire, valoarea din fiecare obiect
        // de tip <nume_fis, rang, lungime, nrCuv>
        for (int i = 0; i < readInput.getNrFiles(); i++) {
                String aux = String.valueOf(list.get(i));
                aux = aux.substring(12);
                writer.write(aux);
                writer.write("\n");
        }
        writer.close();
    }
}
