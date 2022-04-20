public class DataService {
    Node head; //head of list
    Node tail; //tail of list
    int size = size();


    public DataService() {

    }

    //Check list is empty or not
    public boolean isEmpty() {
        boolean empty;
        if (head ==  null){
            empty = true;
        }
        else
            empty=false;
        return empty;
    }

    //This function first builds new patient with the given data than adds it to the LinkedList
    public void insert(int data) {
        if (isEmpty())
            head= tail = new Node(data);
        else{
            tail.next = new Node(data);
            Node temp = tail;
            tail = tail.next;
            tail.previous = temp;

        }
        size++;
    }

    //This method finds how many patients are exist in LinkedList
    public int size() {
        Node tempSize = head;
        int size = 0;
        while (tempSize != null)
        {
            size++;
            tempSize = tempSize.next;
        }

        return size;
    }

    //This function get the node in the given index
    public Node getByPosition(int pos){
        if(this.isEmpty())
            return new Node(0);
        Node current = this.head;
        int index = 0;

        while (current != null){
            if(index++ == pos)
                break;
            current = current.next;
        }

        if(current == null)
            return new Node(0);
        return current;
    }

    public void print() {
        if(this.isEmpty())
            return;
        Node node = this.head;
        int index = 1;

        System.out.println("__________________");
        while (node != null) {
            if (size == 24){
                System.out.println("|\s"+index++ + ". Month | " + node.getData()+" |");
                System.out.println(" ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯");
            }
            else if (size == 48 ){
                System.out.println("|\s"+index++ + ". 15days | " + node.getData()+" |");
                System.out.println(" ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯");
            } else {
                System.out.println("|\s"+index++ + ". 10days | " + node.getData()+" |");
                System.out.println(" ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯");
            }
            node = node.next;
        }
    }
}
