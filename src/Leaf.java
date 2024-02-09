import java.util.List;

class Leaf extends Node {
    private List<Byte> data;
    public Leaf(List<Byte> data,int frequency){
        super(frequency);
        this.data=data;
    }

    public List<Byte> getData() {
        return data;
    }

    public void setData(List<Byte> data) {
        this.data = data;
    }
}
