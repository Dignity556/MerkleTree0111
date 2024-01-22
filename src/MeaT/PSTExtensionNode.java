package MeaT;

public class PSTExtensionNode {
    private PSTBranchNodeItem pre_item;
    private MerkleGraphTree root_item;
    private String property;
    private PSTBranchNode next_item;
    private String id;

    public PSTBranchNodeItem getPre_item() {
        return pre_item;
    }

    public void setPre_item(PSTBranchNodeItem pre_item) {
        this.pre_item = pre_item;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public PSTBranchNode getNext_item() {
        return next_item;
    }

    public void setNext_item(PSTBranchNode next_item) {
        this.next_item = next_item;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void set_next_branch(PSTBranchNode branchNode){
        this.next_item=branchNode;
        branchNode.setPrevous(this);
    }

    public void set_pre_branchitem(PSTBranchNodeItem bnodeItem){
        this.pre_item=bnodeItem;
        bnodeItem.setNext_extension(this);
    }

    public MerkleGraphTree getRoot_item() {
        return root_item;
    }

    public void setRoot_item(MerkleGraphTree root_item) {
        this.root_item = root_item;
    }
}
