/**
    Clasa este folosite pentru a sorta obiectele in care s-au 
    pus numele fisierului, rangul fis, lungimea cuvantului maxim,
    cat si nr de aparitii ale cuvantului
 */
public class WriteOutput implements Comparable<WriteOutput>{
    private String fileName;
    private Float rang;
    private Integer lungime;
    private Integer nrCuv;


    public WriteOutput(String fileName, String rang, Integer lungime, Integer nrCuv) {
        this.fileName = fileName;
        this.rang = Float.valueOf(rang);
        this.lungime = lungime;
        this.nrCuv = nrCuv;
    }

    @Override
    public String toString() {
        return fileName +
                "," + rang +
                "," + lungime +
                "," + nrCuv;
    }

    /**
     * Se compara in functie de rang descrescator
     */
    @Override
    public int compareTo(WriteOutput o) {
        if (this.rang > o.rang) {
            return -1;
        } else if (this.rang < o.rang){
            return 1;
        }
        return 0;
    }
}
