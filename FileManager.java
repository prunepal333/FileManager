package FileManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

class MyFile {
    String name;
    int numOfChars;
    SectorList sectors;
    public String toString()
    {
        return "[" + name + "," + numOfChars + ", " + sectors + "]";
    }
}
class Sector {
    int lo, hi;

    public Sector() {

    }

    public Sector(int lo, int hi) {
        this.lo = lo;
        this.hi = hi;
    }
    public String toString()
    {
        return "[" + lo + ", " + hi + "]";
    }
}

class SectorList extends LinkedList<Sector> {
    private static final long serialVersionUID = 10324242;

    SectorList() {
    }
    SectorList(Sector s)
    {
        this.add(s);
    }
}

class FileList extends LinkedList<MyFile> {
    private final static long serialVersionUID = 1032424;

    public MyFile searchFileByName(String filename) {
        return null;
    }
}

class FileManager {
    final int numberOfSectors = 256; // number of sectors in the disk
    final int sizeOfSector = 3; // number of characters, a sector can have
    char disks[];
    protected SectorList pool;
    protected FileList files;
    static Scanner sc = new Scanner(System.in);

    public FileManager() {
        disks = new char[numberOfSectors * sizeOfSector];
        pool = new SectorList(new Sector(0, numberOfSectors - 1));
        files = new FileList();
    }

    public void save(String contents) {
        SectorList sectors = new SectorList();
        Sector sector;
        int requiredBlockSize = contents.length() / sizeOfSector;
        for (int i = 0; i < pool.size(); i++) {
            sector = pool.get(i);
            if (requiredBlockSize < (sector.hi - sector.lo + 1)) {
                sectors.add(new Sector(sector.lo, sector.lo + requiredBlockSize - 1));
                sector.lo = sector.lo + requiredBlockSize;
                break;
            }
            requiredBlockSize -= (sector.hi - sector.lo + 1);
            sectors.add(new Sector(sector.lo, sector.hi));
            pool.remove(i);
        }

        // filling file information
        MyFile file = new MyFile();
        System.out.println("Enter the filename to be saved as: ");
        file.name = sc.next();
        file.numOfChars = contents.length();
        file.sectors = sectors;

        // performing disk operation
        diskInsert(sectors, contents);

        // adding file to the files list.
        files.add(file);
    }
    public void diskInsert(SectorList sectors, String contents)
    {
        Sector sector;
        for (int i = 0; i < sectors.size(); i++) {
            sector = sectors.get(i);
            int count = 0;
            for (int j = sector.lo; j <= sector.hi; j++) {
                for (int k = 0; k < sizeOfSector; k++) {
                    if (count + k >= contents.length())
                    {
                        return;
                    }
                    disks[k + j * sizeOfSector] = contents.charAt(count + k);
                }
                count += sizeOfSector;
            }
        }
    }
    public void delete(String filename) {
        MyFile file;
        file = files.searchFileByName(filename);
        if (file == null) {
            System.out.println("⥸⥸File doesn't exist⥺⥷");
            return;
        }
        for (int i = 0; i < file.sectors.size(); i++) {
            pool.add(file.sectors.get(i));
        }
        file = null;
    }
    public void status()
    {
        System.out.println("Disk: " + Arrays.toString(disks));
        System.out.println("Pool: " + pool);
        System.out.println("Files: " + files);
    }
    public void defragment()
    {
        //to be continued...
    }
    public void run() throws IOException
    {
        System.out.println("*********************File Manager************************");
        System.out.println("1. Save to buffer\n" +
                            "2. Delete\n" + 
                            "3. Defragment\n"
        );
        System.out.print("Enter your choice: ");
        int choice = sc.nextInt();
        switch(choice)
        {
            case 1:
                System.out.println("Enter filename to be saved: ");
                String filename = sc.next();
                
                BufferedReader br = new BufferedReader(new FileReader(filename));
                String contents = "";
                String s;
                //read all lines into single string
                while((s = br.readLine()) != null)  contents += s;
                br.close();
        
                save(contents);
                break;    
            case 2:
                System.out.println("Enter the file to be deleted: ");
                String deleteFileName = sc.next();
                delete(deleteFileName);
                break;
            case 3:
                defragment();
                break;
            case 4:
                status();
                break;
            case 5:
                System.exit(0);
            default:
                System.out.println("Invalid option\n\n");
        }
    }
}