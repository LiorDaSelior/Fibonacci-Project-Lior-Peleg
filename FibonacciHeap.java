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

		
		private static int countLinks=0;
		private static int countCuts=0;

        private static double phi = (1 + Math.sqrt(5))/2; //The golden ratio
		
		
		
	//Constructor of Fibonacci Heap!
	public FibonacciHeap() {
			this.min=null;
			this.first=null;
			this.numTrees=0;
			this.size=0;
			this.countMarkNodes=0;
			
		}
	
	public HeapNode getFirst() {
		return this.first;
	}
		
	public int getSize() {
		return this.size;
	}

    public int getNumTrees() {
		return this.numTrees;
	}

    public int getCountMarkNodes() {
		return this.countMarkNodes;
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
    * private replaceMin(HeapNode node)
    *
    * Only used for insert. If argument node has minimal key, update min attr in Heap.
    * 
    * Returns true if min was replaced, false otherwise.
    *
    * @pre: node is in heap, node.getParent() == null (node is root node).
    */
    private boolean replaceMin(HeapNode node) {
        if (this.min != null && node.getKey() > this.min.getKey()) {
            this.min = node;
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
        HeapNode temp = new HeapNode(key);
        if (this.first != null) {
            this.first.insertBefore(temp);
        }
        this.first = temp;
        this.size++;
        this.numTrees++;
        this.replaceMin(temp);
    	return temp;
    }

       /**
    * private removeMinNode()
    *
    * Only used for deleteMin. Remove the node with minimal key, add children as heap roots.
    *
    * @pre: there is more than 1 node in Heap.
    */
    public void removeMinNode() {
        int children_amount;
        int reset_mark_amount;
        HeapNode minNode = this.min;
        if (this.numTrees == 1) { // If there is only one tree and heap has 2+ nodes therefore min has child.
            this.first = minNode.child;
            this.min = minNode.child;
            reset_mark_amount = this.min.ResetMarkedInChain();
            children_amount = this.min.nulifyParentInChain();
            this.numTrees = this.numTrees - 1 + children_amount;
            this.countMarkNodes = this.countMarkNodes - reset_mark_amount;
		    this.size--;
        }
        else { // If there 2+ trees, min can be a lone root.
           if (minNode.child == null) {
                if (minNode == this.first) {
                    this.first = this.min.next;
                }
           }
           else {
                reset_mark_amount = this.min.child.ResetMarkedInChain();
                children_amount = this.min.child.nulifyParentInChain();
                minNode.insertBefore(minNode.child, minNode.child.prev);
                if (minNode == this.first) {
                    this.first = minNode.child;
                }
                this.numTrees = this.numTrees + children_amount;
                this.countMarkNodes = this.countMarkNodes - reset_mark_amount;
           }
           HeapNode rightBrother = this.min.next;
           HeapNode leftBrother = this.min.prev;
           rightBrother.prev = leftBrother;
           leftBrother.next = rightBrother;
           this.min = this.first; //it doesn't matter who is considered min before we start consolidate, only that it is a root node
           this.numTrees--;
           this.size--;
        }
    }

    private HeapNode consolidateConnect(HeapNode node1, HeapNode node2) {
        var minHeapNode = node1.key < node2.key ? node1 : node2;
		var maxHeapNode = node1.key > node2.key ? node1 : node2;
        minHeapNode.mark = false;
        this.countMarkNodes--;
        var temp = minHeapNode.getChild();
        minHeapNode.setChild(maxHeapNode);
        maxHeapNode.setParent(minHeapNode);
        // connect children
        if (temp == null) {
            maxHeapNode.setNext(maxHeapNode);
            maxHeapNode.setPrev(maxHeapNode);
        }
        else {
            maxHeapNode.setPrev(temp.getPrev());
            temp.getPrev().setNext(maxHeapNode);

            temp.setPrev(maxHeapNode);
            maxHeapNode.setNext(temp);
        }
        minHeapNode.setRank(minHeapNode.getRank()+1);
        countLinks++;
        return minHeapNode;
    }

    public void consolidate() {
        HeapNode[] heapArr = new HeapNode[(int)(Math.log(this.size)/Math.log(FibonacciHeap.phi)) + 1];
        HeapNode currNode = this.first;
        HeapNode tempNode;
        int currRank;
        for (int i = 0; i < this.numTrees; i++) {
            tempNode = currNode;
            currNode = currNode.next;
            tempNode.next = tempNode;
            tempNode.prev = tempNode;
            currRank = tempNode.getRank();
            while (heapArr[currRank] != null) {
                tempNode = consolidateConnect(heapArr[currRank], tempNode);
                this.numTrees--;
                currRank++;
            }
            heapArr[currRank] = tempNode;
        }
        this.min=null;
        this.first=null;
        for (HeapNode heapNode : heapArr) {
            if (heapNode != null) {
                if (this.first==null) {
                    this.first = heapNode;
                }
                else {
                    this.first.insertBefore(heapNode);
                }
                replaceMin(heapNode);
            }
        }
    }

   /**
    * public void deleteMin()
    *
    * Deletes the node containing the minimum key.
    *
    */
    public void deleteMin()
    {
        if (this.isEmpty()) {return;}
        if (this.size == 1) { //If empty no need to do anything
            this.min = null;
            this.first = null;
            this.size--;
            this.numTrees--;
            return;
        }
        this.removeMinNode();
        this.consolidate();

        // HeapNode[] heapArr = new HeapNode[(int)(Math.log(this.size)/Math.log(FibonacciHeap.phi)) + 1];

        // else if (this.size == 1) {
            
        // }
        // else {

        // }
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
        if (this.first != null && heap2.first != null) {
            HeapNode node1 = this.first;
            HeapNode node2 = this.first.getPrev();
            heap2.first.insertBefore(node1, node2);
            this.size = this.size + heap2.size;
            this.numTrees = this.numTrees + heap2.numTrees;
            this.countMarkNodes = this.countMarkNodes + heap2.countMarkNodes;
            this.replaceMin(heap2.min);
        }
        else if (this.first == null && heap2.first != null) {
            this.first = heap2.first;
            this.size = heap2.size;
            this.min = heap2.min;
            this.numTrees = heap2.numTrees;
            this.countMarkNodes = heap2.countMarkNodes;
        }
        		
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
    	return countLinks;
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
    	int [] res= new int[k];
    	
    	//if H is empty Heap
    	if(H.isEmpty()==true) {
    		
    		return res;
    	}
    	
    	FibonacciHeap helperFib=new FibonacciHeap();

    	HeapNode minFibH=H.findMin();
    	HeapNode addedNode=helperFib.insert(minFibH.getKey());
    	addedNode.setKMinPointer(minFibH);
    	for (int i=0;i<k;i++) {
    		minFibH=helperFib.findMin();
    		res[i]=minFibH.getKey();
    		helperFib.deleteMin();
    		
    		HeapNode currMinHelpKMinPTR=minFibH.getKMinPointer();
    		if(currMinHelpKMinPTR.getChild()!=null) {
    			HeapNode currMinHelpKMinPTRSon=currMinHelpKMinPTR.getChild();
    			do {
    				addedNode=helperFib.insert(currMinHelpKMinPTRSon.getKey());
    				addedNode.setKMinPointer(currMinHelpKMinPTRSon);
    				currMinHelpKMinPTRSon=currMinHelpKMinPTRSon.getNext();
    			}
    			while (currMinHelpKMinPTRSon!=currMinHelpKMinPTR.getChild());
    				
    		}
    	}
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
        private HeapNode KMinPointer;


    	//Constructor of HeapNode
    	
    	public HeapNode(int key) { 
    		this.key = key;
    		this.rank=0;
    		this.mark=false;
    		this.child=null;
    		this.parent=null;
    		this.next=this;
    		this.prev=this;
            this.KMinPointer = null;
    		
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

        public HeapNode getKMinPointer() {
    		return this.KMinPointer;
    	}
        public void setKMinPointer(HeapNode node) {
    		this.KMinPointer=node;
    	}

        private void insertBefore(HeapNode node) {
            HeapNode temp = this.prev;
            node.next = this;
            this.prev = node;
            temp.next = node;
            node.prev = temp;
        }

        private void insertBefore(HeapNode node1, HeapNode node2) {
            HeapNode temp = this.prev;
            node2.next = this;
            this.prev = node2;
            temp.next = node1;
            node1.prev = temp;
        }

        private int nulifyParentInChain() {
            HeapNode target = this;
            int count = 0;
            do {
                count++;
                target.parent = null;
                target = target.next;
            } while (target != this);
            return count;
        }

        private int ResetMarkedInChain() {
            HeapNode target = this;
            int count = 0;
            do {
                if (target.mark) {
                    count++;
                    target.mark = false;
                }
                target = target.next;
            } while (target != this);
            return count;
        }
        
    }
/* 
    private class KMinHeapNode extends HeapNode{
        private HeapNode originalNode;
        private KMinHeapNode(HeapNode originalHeapNode) {
            super(originalHeapNode.key);
            this.originalNode = originalHeapNode;
        }
    
    } */
}
