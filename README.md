# LZWCompression

In this project we used the LZW compression algorithm to compress and expand files. 
What I have done is implemented three different modes for this compression algorithm : 
  1. **Do Nothing mode**  Do nothing and continue to use the full codebook (this is already implemented by LZW.java).
  2. **Reset mode** Reset the dictionary back to empty so that new codewords can be added. Be careful to reset at 
     the appropriate place for both compression and expansion, so that the algorithms remain in sync.  
     This is very tricky and may require alot of planning in order to get it working correctly.
  3. 
