import java.util.List;
import java.util.Objects;

public class Batch {

    private String batchNo;
    private int noOfComponents;
    private String componentType;
    private String sizeModel;
    private List<Component> componentList;

    public Batch(String batchNo, int noOfComponents, String componentType, String sizeModel, List<Component> componentList) {
        this.batchNo = batchNo;
        this.noOfComponents = noOfComponents;
        this.componentType = componentType;
        this.sizeModel = sizeModel;
        this.componentList = componentList;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public int getNoOfComponents() {
        return noOfComponents;
    }

    public void setNoOfComponents(int noOfComponents) {
        this.noOfComponents = noOfComponents;
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    public String getSizeModel() {
        return sizeModel;
    }

    public void setSizeModel(String sizeModel) {
        this.sizeModel = sizeModel;
    }

    public List<Component> getComponentList() {
        return componentList;
    }

    public void setComponentList(List<Component> componentList) {
        this.componentList = componentList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Batch batch = (Batch) o;
        return noOfComponents == batch.noOfComponents &&
                Objects.equals(batchNo, batch.batchNo) &&
                Objects.equals(componentType, batch.componentType) &&
                Objects.equals(sizeModel, batch.sizeModel) &&
                Objects.equals(componentList, batch.componentList);
    }

    @Override
    public int hashCode() {

        return Objects.hash(batchNo, noOfComponents, componentType, sizeModel, componentList);
    }

    @Override
    public String toString() {
        return "Batch{" +
                "batchNo='" + batchNo + '\'' +
                ", noOfComponents=" + noOfComponents +
                ", componentType='" + componentType + '\'' +
                ", sizeModel='" + sizeModel + '\'' +
                ", componentList=" + componentList +
                '}';
    }
}
