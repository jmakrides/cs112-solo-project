import java.util.Objects;

public class Component {

    private String batchNo;
    private String serialNo;
    private String componentType;
    private String componentSizeModel;

    public Component(String batchNo, String serialNo, String componentType, String componentSizeModel) {
        this.batchNo = batchNo;
        this.serialNo = serialNo;
        this.componentType = componentType;
        this.componentSizeModel = componentSizeModel;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    public String getComponentSizeModel() {
        return componentSizeModel;
    }

    public void setComponentSizeModel(String componentSizeModel) {
        this.componentSizeModel = componentSizeModel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Component component = (Component) o;
        return Objects.equals(batchNo, component.batchNo) &&
                Objects.equals(serialNo, component.serialNo) &&
                Objects.equals(componentType, component.componentType) &&
                Objects.equals(componentSizeModel, component.componentSizeModel);
    }

    @Override
    public int hashCode() {

        return Objects.hash(batchNo, serialNo, componentType, componentSizeModel);
    }

    @Override
    public String toString() {
        return "Component{" +
                "batchNo='" + batchNo + '\'' +
                ", serialNo='" + serialNo + '\'' +
                ", componentType='" + componentType + '\'' +
                ", componentSizeModel='" + componentSizeModel + '\'' +
                '}';
    }
}
