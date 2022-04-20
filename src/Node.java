public class Node extends DataService{
    private int data;
    Node next;
    Node previous;
    DataService dataService;
    //Constructor
    public Node(int n){
        this.data = n;
        this.next=null;
        this.previous=null;
    }

    public Node(DataService dataService){

        this.next= head.next;
        this.previous=head.previous;
    }
    //Getter method
    public int getData(){
        return this.data;
    }

    public void setData(int data) {
        this.data = data;
    }
}
