import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ReadInput {
    private String input; // fisierul din care citesc alte fisiere
    private String output; // fisierul in care afisez
    private int nrThread; // nr de thread-uri pe care le pornesc

    private int dimFragment; // pe cati octeti impart fiecari fisier
    private int nrFiles; // nr de fisiere din care voi citi din fisierul mare
    // cu fisiere
    private ArrayList<String> inputFiles; // fisierele din care voi citi si
    // voi imparti treaba pe tread-uri
    // ca sa stiu cate task-uri trebuie sa pornesc pe fiecare fisier
    private int nrTaskuri;

    public ReadInput() {}

    public ReadInput(String nrThread, String input, String output) {
        this.input = input;
        this.output = output;
        try {
            this.nrThread = Integer.parseInt(nrThread);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void readInputFile() {
        //  citim datele din fisier input
        inputFiles = new ArrayList<>();
        try {
            File myObj = new File(input);
            Scanner myReader = new Scanner(myObj);

            String aux = myReader.next();
            this.dimFragment = Integer.parseInt(aux);

            aux = myReader.next();
            this.nrFiles = Integer.parseInt(aux);

            for (int i = 0; i < nrFiles; i++) {
                aux = myReader.next();
                inputFiles.add(aux);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Nu pot sa citesc din fisier");
            e.printStackTrace();
        }
    }

    /**
     * ma uit prin fiecare fisier si intorc cate task-uri pot sa pornesc pe
     * fiecare
     * @return
     */
    public int cateTaskPornesc(String numeFisier) {
        File myObj = new File(numeFisier);
        return (int) myObj.length();
    }

    public int getNrThread() {
        return nrThread;
    }

    public int getDimFragment() {
        return dimFragment;
    }

    public int getNrFiles() {
        return nrFiles;
    }

    public ArrayList<String> getInputFiles() {
        return inputFiles;
    }

    @Override
    public String toString() {
        return "ReadInput{" +
                "input='" + input + '\'' +
                ", output='" + output + '\'' +
                ", nrThread=" + nrThread +
                '}';
    }
}
