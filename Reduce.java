import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Reduce extends Thread {
    private Task[] tasks;
    private ArrayList<String> files;
    private int id;
    private int nrThread;
    private int nrFis;
    private int nrTask;

    // pentru fiecare fisier
    private ArrayList<ConcurrentHashMap<Integer, Integer>> listMap;
    private ArrayList<Integer> maxList;
    private ArrayList<Integer> nrMaxList;
    private ArrayList<String> listRang;
    private ArrayList<Integer> nrTotalCuv;
    private ArrayList<String> numeFisier;

    public Reduce(Task[] tasks, ArrayList<String> files, int id, int nrThread
            ,int nrTask) {
        this.tasks = tasks;
        this.files = files;
        this.id = id;
        this.nrThread = nrThread;
        this.nrFis = files.size();
        this.nrTask = nrTask;
        listMap = new ArrayList<>();
        maxList = new ArrayList<>();
        nrMaxList = new ArrayList<>();
        listRang = new ArrayList<>();
        nrTotalCuv = new ArrayList<>();
        numeFisier = new ArrayList<>();
    }

/**
    Functia apelata de fiecare fir de executie
 */
    @Override
    public void run() {
        // functie ce prelucreaza toate map-urile dintr-un fisier
        seteazaMapCorect();
        // dupa ce am setat map-ul final voi calcula rangul fisierului
        calculeazaRang();
    }

/**
    Dupa ce fiecare task dintr-un fisier are propriul map, e timpul
    sa unesc toate map urile in unul singur, pe fiecare fisier:
    in1.txt: (5, 1), (5, 2), (6, 1) va rezulta in (5, 3), (6, 1)
 */
    public void seteazaMapCorect() {
        int start = (int) (id * (double)nrFis / nrThread);
        int end = (int) Math.min((id + 1) * (double)nrFis / nrThread,
                nrFis);

        // pentru fiecare fisier pe care il task-ul de reduce
        // in1.txt in2.txt in3.txt
        for (int i = start; i < end; i++) {
            // ex: in1.txt
            String fisier = files.get(i);
            ConcurrentHashMap<Integer, Integer> map = new ConcurrentHashMap<>();

            // trec prin toate task
            int max = -1;
            int nrMaxCuv = -1;
            int nrCuvTotale = 0;
            for (int j = 0; j < nrTask; j++) {
                String numeTaskFis = tasks[j].getNumeFisier();
                if (numeTaskFis.equals(fisier)) {
                    // acum tot ce trebuie sa fac este ca intr-un map
                    // sa pun toate valorile acestea si sa le leg la un map
                    // final
                    ConcurrentHashMap<Integer, Integer> aux = tasks[j].getMap();
                    for (ConcurrentHashMap.Entry<Integer, Integer> entry : aux.entrySet() ) {
                        int key = entry.getKey();
                        int value = entry.getValue();
                        // setez cuvantul de lungime maxim
                        if (key > max) {
                            max = key;
                        }
                        // daca am deja cuvantul
                        // adun valorile vechi pe care le-a avut
                        // ex {5, 2} apoi vede alt {5, 3}
                        // rezulta la final {5, 2+3} = {5, 5}
                        if (map.containsKey(key)) {
                            int size = map.get(key) + value;
                            map.put(key, size);
                        } else {
                            map.put(key, value);
                        }

                        // setez numarul de aparitii ale
                        // celui mai lung cuvant
                        if (map.containsKey(max)) {
                            nrMaxCuv = map.get(max);
                        }
                    }
                }
            }
            // adun nr de cuvinte totale
            for (ConcurrentHashMap.Entry<Integer, Integer> entry : map.entrySet() ) {
                int value = entry.getValue();
                nrCuvTotale += value;
            }
            // setez map, lungime, nrCuv pentru a fi afisate la output
            listMap.add(map);
            maxList.add(max);
            nrMaxList.add(nrMaxCuv);
            nrTotalCuv.add(nrCuvTotale);
            numeFisier.add(fisier);
        }
    }

/**
    Dupa ce am setat mapul corect pentru fiecare fisier, lungimea cuvantului
    maximal, si nr de aparitii ale acestuia, voi calcula rangul fiecarui fisier,
    folosind fibonnaci
 */
    public void calculeazaRang() {
        float sum = 0f;
        for (int i = 0; i < listMap.size(); i++) {
            // acum ajung pe map
            ConcurrentHashMap<Integer, Integer> map = listMap.get(i);
            for(ConcurrentHashMap.Entry<Integer, Integer> entry :
                    map.entrySet()) {
                int lungime = fib( entry.getKey() + 1);
                int nrAparitii = entry.getValue();

                sum += (lungime * nrAparitii);
            }
            int nrCuv = nrTotalCuv.get(i);
            float rangFinal = sum / nrCuv;
            listRang.add(String.format("%.2f", rangFinal));
            sum = 0f;
        }
    }

/**
    functia ce intoarce al n - lea termen din sirul lui fibonnaci
 */
    private int fib(int n) {
        if (n <= 1)
            return n;
        return fib(n-1) + fib(n-2);

    }

    @Override
    public long getId() {
        return id;
    }

    public ArrayList<Integer> getMaxList() {
        return maxList;
    }

    public ArrayList<Integer> getNrMaxList() {
        return nrMaxList;
    }

    public ArrayList<String> getListRang() {
        return listRang;
    }

    public ArrayList<String> getNumeFisier() {
        return numeFisier;
    }

    @Override
    public String toString() {
        return "Reduce{" +
                ", numeFisier= " + numeFisier +
                ", listRang= " + listRang +
                "lungime " + maxList +
                "nr cuv " + nrMaxList +
                '}';
    }
}
