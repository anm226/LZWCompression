/*************************************************************************
 *  Compilation:  javac MyLZW.java
 *  Execution:    java MyLZW - < input.txt   (compress)
 *  Execution:    java MyLZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *  WARNING: STARTING WITH ORACLE JAVA 6, UPDATE 7 the SUBSTRING
 *  METHOD TAKES TIME AND SPACE LINEAR IN THE SIZE OF THE EXTRACTED
 *  SUBSTRING (INSTEAD OF CONSTANT SPACE AND TIME AS IN EARLIER
 *  IMPLEMENTATIONS).
 *
 *  See <a href = "http://java-performance.info/changes-to-string-java-1-7-0_06/">this article</a>
 *  for more details.
 *
 *************************************************************************/

public class MyLZW {
    private static final int R = 256;        // number of input chars
    private static final int L = 65536;      // number of codewords = 2^W
    private static int W = 9;                // codeword width
    private static int compressed = 0;
    private static int inputted = 0;
    private static float ratio;
    private static float oldRatio;  //variable used for monitor mode
    private static float trueRatio; //variable used for monitor mode
    private static boolean doIt = false;
    private static boolean moniter = false;
    //orignal compress method
    public static void compress() {

        String input = BinaryStdIn.readString();
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF
		int y = 0;
        while (input.length() > 0) {

            String s = st.longestPrefixOf(input);  // Find max prefix match s.
            BinaryStdOut.write((y=st.get(s)), W);      // Print s's encoding.

            int t = s.length();
            codeWidth(code);						//changes codeword width
            if (t < input.length() && code < L)    // Add s to symbol table.
                st.put(input.substring(0, t + 1), code++);

            input = input.substring(t);            // Scan past s in input.

        }

        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    }
    //original expand method
    public static void expand() {
        String[] st = new String[L];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = BinaryStdIn.readInt(W);
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];

        while (true) {

            BinaryStdOut.write(val);
            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];

            if (i == codeword) s = val + val.charAt(0);   // special case hack

            if (i < L) st[i++] = val + s.charAt(0);
            codeWidth(i);									//changes codeword width
            val = s;



        }
        BinaryStdOut.close();
    }
    //compress method based on reset algorithm
    public static void compressR() {
        String input = BinaryStdIn.readString();
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF

		int x = 0;
		int y = 0;
        while (input.length() > 0) {

            String s = st.longestPrefixOf(input);  // Find max prefix match s.
            BinaryStdOut.write((y=st.get(s)), W);      // Print s's encoding.
            int t = s.length();
            codeWidth(code);
            if(code==L){									//resets codebook if full
                   W = 9;
            	   st = new TST<Integer>();
            		for (int i = 0; i < R; i++)
           				 st.put("" + (char) i, i);
       				code = R+1;

       		}

            if (t < input.length() && code < L)    // Add s to symbol table.
                st.put(input.substring(0, t + 1), code++);



            input = input.substring(t);            // Scan past s in input.




        }

        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    }
    //expand method based on reset algorithm
    public static void expandR() {
        String[] st = new String[L];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

         int x = 0;
        int codeword = BinaryStdIn.readInt(W);
        int l = 0;
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];

         while (true) {

            BinaryStdOut.write(val);
            if(i==L){								//resets codebook if full
            	W = 9;
                st = new String[L];
                for (i = 0; i < R; i++)
                st[i] = "" + (char) i;
                 st[i++] = "";
            }

            codeword = BinaryStdIn.readInt(W);

            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack

            if (i < L) st[i++] = val + s.charAt(0);
             codeWidth(i);


            val = s;



            }

        BinaryStdOut.close();
    }
    //compress method based on monitor algorithm
    public static void compressM() {
        String input = BinaryStdIn.readString();
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF
		int x = 0;
		int l = 0;
        while (input.length() > 0) {

            String s = st.longestPrefixOf(input);  // Find max prefix match s.
            inputted = inputted + (s.length()*8);
            BinaryStdOut.write(st.get(s), W);      // Print s's encoding.

            int t = s.length();


            ratio = (float)inputted/compressed;


            trueRatio = oldRatio/ratio;
            if(L==code&&x==0){
            	oldRatio = ratio;
            	x=1;
            }						//start monitoring


            if(trueRatio>1.1){
              	   W = 9;
            	   st = new TST<Integer>();
            		for (int i = 0; i < R; i++)
           				 st.put("" + (char) i, i);
       				code = R+1;


       				trueRatio=0;
       				oldRatio = 0;
       				ratio= 0;
       				x=0;

            }						//reset codebook if oldRatio/newratio > 1.1

            codeWidth(code);
            compressed = compressed+W;
            if (t < input.length() && code < L){    // Add s to symbol table.
                st.put(input.substring(0, t + 1), code++);
            }

            input = input.substring(t);            // Scan past s in input.




        }

        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    }
    //expand method based on the monitor algorithm
    public static void expandM() {
        String[] st = new String[L];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

         int x = 0;
        int codeword = BinaryStdIn.readInt(W);
        int l = 0;
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];

         while (true) {

            BinaryStdOut.write(val);
            inputted = inputted+(val.length()*8);			//bits consumed
            ratio = (float)inputted/compressed;
            trueRatio = oldRatio/ratio;
            if(i==L&&x==0){
            	oldRatio = ratio;
            	x=1;
            }
            if(trueRatio>1.1){							//reset codebook if oldRatio/newRatio > 1.1
            	W = 9;
                st = new String[L];
                for (i = 0; i < R; i++)
                st[i] = "" + (char) i;
                 st[i++] = "";
			    trueRatio = 0;
                oldRatio = 0;
                ratio = 0;
                x = 0;
            }

            codeword = BinaryStdIn.readInt(W);

            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            if (i < L) st[i++] = val + s.charAt(0);
            compressed = compressed+W;						//bits compressed
            codeWidth(i);



            val = s;



            }

        BinaryStdOut.close();
    }

     //returns codeWord width depending on number of codewords.
  	 public static void codeWidth(int n){
     		if(n==512)
            	W = 10;
           	if(n==1024)
            	W = 11;
            if(n==2048)
            	W = 12;
            if(n==4096)
            	W = 13;
            if(n==8192)
            	W = 14;
            if(n==16384)
            	W = 15;
            if(n==32768)
            	W = 16;
     }


    public static void main(String[] args) {
       	//checks what method was used to compress file
        if(args[0].equals("+")){
        	char input = BinaryStdIn.readChar();
        	if(input=='n'){
        		expand();
        	}
        	else if(input=='r'){
        		expandR();
        	}
        	else if(input=='m'){
        		expandM();
        	}

        }
        else if(args[0].equals("-")&& args[1].equals("n")){
        BinaryStdOut.write('n');
        compress();
        }
        else if (args[0].equals("-")&& args[1].equals("r")){
         BinaryStdOut.write('r');
         compressR();

        }
        else if (args[0].equals("-")&& args[1].equals("m")){
         BinaryStdOut.write('m');
         compressM();
         }
        else throw new IllegalArgumentException("Illegal command line argument");
    }

}
