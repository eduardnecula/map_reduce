import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class Task  {
    private String numeFisier;
    private int idTask;
    private int dimensiune;
    private int offset;
    private String newList;
    private ConcurrentHashMap<Integer, Integer> map;
    public Task() {

    }

    public Task(int idTask, String numeFisier,int offset, int dimensiune) throws FileNotFoundException {
        this.idTask = idTask;
        this.numeFisier = numeFisier;
        this.offset = offset;
        this.dimensiune = dimensiune;
        this.map = new ConcurrentHashMap<Integer, Integer>();
        arataFisier();
    }

    public void arataFisier() throws FileNotFoundException {
        File myObj = new File(numeFisier);
        Scanner scanner = new Scanner(myObj);
        scanner.useDelimiter(", \n\t\r;?.");
        ArrayList<String> list = new ArrayList<>();
        while (scanner.hasNext()) {
            list.add(scanner.next());
        }
        String listString = list.toString();
        newList = listString.substring(getOffset() + 1, 1 + getOffset() + getDimensiune());
    }

    public String getNumeFisier() {
        return numeFisier;
    }

    public void setNumeFisier(String numeFisier) {
        this.numeFisier = numeFisier;
    }

    public int getIdTask() {
        return idTask;
    }

    public void setIdTask(int idTask) {
        this.idTask = idTask;
    }

    public synchronized int getDimensiune() {
        return dimensiune;
    }

    public synchronized void setDimensiune(int dimensiune) {
        synchronized (this) {
            this.dimensiune = dimensiune;
        }
    }

    public synchronized int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        synchronized (this) {
            this.offset = offset;
        }
    }

    public ConcurrentHashMap<Integer, Integer> getMap() {
        return map;
    }

    public void setMap(ConcurrentHashMap<Integer, Integer> map) {
        this.map = map;
    }

    public String getNewList() {
        return newList;
    }

    public void setNewList(String newList) {
        this.newList = newList;
    }

    @Override
    public String toString() {
        return "Task{" +
                "numeFisier='" + numeFisier + '\'' +
                ", idTask=" + idTask +
                ", dimensiune=" + dimensiune +
                ", offset=" + offset +
                ", newList='" + newList + '\'' +
                ", map=" + map +
                '}';
    }
}
