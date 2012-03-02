import java.util.ArrayList;

public class GtfsSchemaCreationImpl implements GtfsSchemaCreation{
	private String fname;
	
	public GtfsSchemaCreationImpl(){	
	}
	
	public GtfsSchemaCreationImpl(GtfsSchemaCreationImpl other){
		this.fname = other.getFname();
		this.fields = other.getFields();
		this.isNullAble = other.getIsNullAble();
		this.scales = other.getScales();
		this.sizes = other.getSizes();
		this.types = other.getTypes();
	}
	
	public void deleteColumn(int i){
		deletedColumnHeader = fields.get(i);
		fields.remove(i);
		isNullAble.remove(i);
		sizes.remove(i);
		types.remove(i);
	}
	
	public void setFname(String fname) {
		this.fname = fname;
	}
	
	public String getFname() {
		return fname;
	}

	private ArrayList<String> fields;
	
	public ArrayList<String> getFields() {
		return fields;
	}

	public void setFields(ArrayList<String> fields) {
		this.fields = fields;
	}
	
	private ArrayList<Integer> types;

	public ArrayList<Integer> getTypes() {
		return types;
	}

	public void setTypes(ArrayList<Integer> types) {
		this.types = types;
	}
	
	private ArrayList<Integer> sizes;
	
	public ArrayList<Integer> getSizes() {
		return sizes;
	}

	public void setSizes(ArrayList<Integer> sizes) {
		this.sizes = sizes;
	}

	public ArrayList<Integer> getScales() {
		return scales;
	}

	public void setScales(ArrayList<Integer> scales) {
		this.scales = scales;
	}

	public ArrayList<Boolean> getIsNullAble() {
		return isNullAble;
	}

	public void setIsNullAble(ArrayList<Boolean> isNullAble) {
		this.isNullAble = isNullAble;
	}

	private ArrayList<Integer> scales = null;
	
	private ArrayList<Boolean> isNullAble;
	
	private String deletedColumnHeader = null;
	
	public String getDeletedColumnHeader(){
		return deletedColumnHeader;
	}
}
