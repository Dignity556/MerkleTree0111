package MeaT;

public class PSTExtensionNode {
    private PSTBranchNodeItem pre_item;
    private String property;
    private PSTBranchNode next_item;

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

    public void set_next_branch(PSTBranchNode branchNode){
        this.next_item=branchNode;
        branchNode.setPrevous(this);
    }

    public void set_pre_branchitem(PSTBranchNodeItem bnodeItem){
        this.pre_item=bnodeItem;
        bnodeItem.setNext_extension(this);
    }

}
