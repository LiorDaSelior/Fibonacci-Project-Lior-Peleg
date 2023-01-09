/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap
{
	
	//Fields
		private HeapNode min;
		private HeapNode first;
		
		private int numTrees;
		
		private int size;
		private int countMarkNodes;

		
		private static int countLinksTress=0;
		private static int countCuts=0;
		
		
		
	//Constructor of Fibonacci Heap!
	public FibonacciHeap() {
			this.min=null;
			this.first=null;
			this.numTrees=0;
			this.size=0;
			this.countMarkNodes=0;
			
		}
		
	
   /**
    * public boolean isEmpty()
    *
    * Returns true if and only if the heap is empty.
    *   
    */
    public boolean isEmpty()
    {
    	if (this.size==0) {
    		return true;
    	}
    	return false;
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * The added key is assumed not to already belong to the heap.  
    * 
    * Returns the newly created node.
    */
    public HeapNode insert(int key)
    {    
    	return new HeapNode(key); // should be replaced by student code
    }

   /**
    * public void deleteMin()
    *
    * Deletes the node containing the minimum key.
    *
    */
    public void deleteMin()
    {
     	return; // should be replaced by student code
     	
    }

   /**
    * public HeapNode findMin()
    *
    * Returns the node of the heap whose key is minimal, or null if the heap is empty.
    *
    */
    public HeapNode findMin()
    {
    	return this.min;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Melds heap2 with the current heap.
    *
    */
    public void meld (FibonacciHeap heap2)
    {
    	  return; // should be replaced by student code   		
    }

   /**
    * public int size()
    *
    * Returns the number of elements in the heap.
    *   
    */
    public int size()
    {
    	return this.size;
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
    * (Note: The size of of the array depends on the maximum order of a tree.)  
    * 
    */
    //????
    public int[] countersRep()
    {
        if (this.isEmpty()==true) {
        	int[] res = new int[0];
        	return res;
    	}
        else {
         // Create the array, loop over the root level and count the ranks.
            int[] res = new int[findMaxRank() + 1];
            HeapNode firstNode = this.first;
            res[firstNode.getRank()]++;
            HeapNode iterNode = firstNode.getNext();
            int currNodeRank;
            while(iterNode != firstNode) {
            	currNodeRank=iterNode.getRank();
                res[currNodeRank]++;
                iterNode = iterNode.getNext();
            }
            return res;

        }
    }

    public int findMaxRank() {
    	  int maxRank = this.first.getRank();
 
          HeapNode iterNode = this.first.getNext();
          while(iterNode!=this.first) {
        	  maxRank=Math.max(maxRank, iterNode.getRank());
        	  iterNode=iterNode.getNext();
        	  }
          return maxRank;
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
	* It is assumed that x indeed belongs to the heap.
    *
    */
    public void delete(HeapNode x) 
  //implemented using Decrease-key and delete-min
    {    
    	//this is the min
    	if (this.size==1) {
    		this.min=null;
 
    	}
    	else {
    		int equalToMin=(Math.abs(x.getKey())-Math.abs(this.min.getKey()));//the number we found that equal to min, we have to remember that x.key>min.key
    		int delta=equalToMin+1;
    		this.decreaseKey(x,delta);
    		deleteMin();
    		
    	}

    	this.size--;//?????
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {    
    	//if x is root
    	if (x.getParent()==null) {
    		x.setKey(x.getKey()-delta);
    	}
    	else {
    		x.setKey(x.getKey()-delta);
    		//check if there is violation
    		if (x.getKey()<x.getParent().getKey()) {
    			//cascadingCut(x,x.getParent()); //Complexity O(log(n)) //doesn't implement yet!
    		}
    		
    	}
    	//check if after the decrease we have to change the current min
		if (x.getKey()<this.min.getKey()) {
			this.min=x;
		}
    	
    	
    }

   /**
    * public int nonMarked() 
    *
    * This function returns the current number of non-marked items in the heap
    */
    public int nonMarked() 
    {    
        return this.size-this.countMarkNodes;
    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * 
    * In words: The potential equals to the number of trees in the heap
    * plus twice the number of marked nodes in the heap. 
    */
    public int potential() 
    {    
        return this.numTrees+2*this.countMarkNodes;
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the
    * run-time of the program. A link operation is the operation which gets as input two
    * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
    * tree which has larger value in its root under the other tree.
    */
    public static int totalLinks()
    {    
    	return countLinksTress;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the
    * run-time of the program. A cut operation is the operation which disconnects a subtree
    * from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts()
    {    
    	return countCuts;
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
    * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
    *  
    * ###CRITICAL### : you are NOT allowed to change H. 
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {  
    	int[] res=new int[k];
        return res;
     }

    
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in another file. 
    *  
    */
    public static class HeapNode{
    	
    	//Fields 
    	private int key;
    	private int rank;
    	private boolean mark;
    	private HeapNode child;
    	private HeapNode parent;
    	private HeapNode next;
    	private HeapNode prev;


    	//Constructor of HeapNode
    	
    	public HeapNode(int key) { 
    		this.key = key;
    		this.rank=0;
    		this.mark=false;
    		this.child=null;
    		this.parent=null;
    		this.next=this;
    		this.prev=this;
    		
    	}
    	
    	
    	public int getKey() {
    		return this.key;
    	}
    	public void setKey(int k) {
    		this.key=k;
    	}
    	
     	public int getRank() {
    		return this.rank;
    	}
    	
    	public void setRank(int k) {
    		this.rank=k;
    	} 
    	
    	public boolean getMarked() {
    		return this.mark;
    	}
    	
    	public void setMarked(boolean TF) {
    		this.mark=TF;
    	}
    	
    	public HeapNode getChild() {
    		return this.child;
    	}
    	
    	public void setChild(HeapNode node) {
    		this.child=node;
    	}
    	
    	public HeapNode getParent() {
    		return this.parent;
    	}
    	
    	public void setParent(HeapNode node) {
    		this.parent=node;
    	}
    	
    	public HeapNode getNext() {
    		return this.next;
    	}
    	
    	public void setNext(HeapNode node) {
    		this.next=node;
    	}
    	
    	public HeapNode getPrev() {
    		return this.prev;
    	}
    	
    	public void setPrev(HeapNode node) {
    		this.prev=node;
    	}
    }
        private class KMinHeapNode extends HeapNode{
            private HeapNode originalNode;
            private KMinHeapNode(HeapNode originalHeapNode) {
                super(originalHeapNode.key);
                this.originalNode = originalHeapNode;
            }
        
    }
}
