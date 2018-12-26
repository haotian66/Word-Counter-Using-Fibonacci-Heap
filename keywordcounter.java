
import java.io.*;
import java.util.*;


public class keywordcounter{

    public static void main(String[] args)throws IOException{
        String input = args[0];
        String output = "output_file.txt";
        File inputfile = new File(input);
        File outputfile = new File(output);
        FileReader in = new FileReader(inputfile);
        FileWriter out = new FileWriter(outputfile);
        BufferedReader br = new BufferedReader(in);
        BufferedWriter bw = new BufferedWriter(out);
        String reg1 = "((\\p{Punct}{1})((\\p{Alpha}|\\p{Punct})+)(\\s)(\\d+))"; // define a regularization formula for matching keywords
        String reg2 = "(\\d+)"; // define a regularization formula for matching queries
        int count = 1;
        Map<String, node> hashtable = new HashMap<String, node>();
        fibonacci heap = new fibonacci();
        String line = br.readLine().replaceAll("\r", "").replaceAll("\n", "").trim(); //read a line from the input file and eliminate all space and symbols
        while (!line.equals("stop"))  //check whether the line indicates stop
        {
            if(line.matches(reg1)) {                   //If the line is a keyword
                line = line.substring(1);  //delete the $ symbol at the beginning of the line
                String[] newl = line.split(" "); //split the line to get words and frequencies
                String name = newl[0];
                int v = Integer.valueOf(newl[1]);
                if(hashtable.containsKey(name)){
                    node t = hashtable.get(name);
                    try {
                        heap.increasekey(t,v);
                    }
                    catch(Exception e) {
                        System.out.println(count+line);
                    }//perform increase key operation corresponding to the word
                } else{
                    node n = new node(name,v);
                    hashtable.put(name, n);   //put the new word into hash table
                    heap.insert(n);   //insert the new node corresponding the word to the top-level list of fibonacci heap
                }
            } else if(line.matches(reg2)){   //If the line is a query
                int number = Integer.valueOf(line);
                int i = 0;
                List<node> nodel = new ArrayList<node>();//Use an arraylist to store the max we removed from fibonacci heap, because we need to insert them back after the query
                while (i<number){
                    node max = heap.removemax();  //perform remove max
                    hashtable.remove(max.label);
                    nodel.add(new node(max.label, max.value));
                    if(i<number-1)
                    {
                        bw.write(max.label + ",");  //write the max frequency words in order
                    } else {
                        bw.write(max.label + "\r\n");
                    }
                    i = i+1;
                }
                i = 0;
                while (i < nodel.size()) {
                    heap.insert(nodel.get(i));//insert the removed nodes back
                    hashtable.put(nodel.get(i).label, nodel.get(i)); //also put them back to hash table
                    i = i + 1;
                }
            }
            line = br.readLine().replaceAll("\r", "").replaceAll("\n", "").trim();
            count = count + 1;
        }
        br.close();
        bw.close();
    }
}
