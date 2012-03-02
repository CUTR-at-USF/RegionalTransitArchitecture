import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;

import com.esri.sde.sdk.client.SeColumnDefinition;
import com.esri.sde.sdk.client.SeException;
import com.esri.sde.sdk.client.SeLayer;

/**
 * @author Khoa Tran
 *
 */
public class Test {
	
	private static Logger _log = LoggerFactory.getLogger(Test.class);

	/**
	 * Main program to create tables
	 * @param args
	 */
	public static void main(String[] args) {
		
		org.apache.log4j.BasicConfigurator.configure();
		
		// TODO Auto-generated method stub
		BeanFactory factory = new XmlBeanFactory(new FileSystemResource("data-source.xml"));
		
		ArcSDEConnection conn = new ArcSDEConnection("d7gis@dot.state.fl.us",
				"sde:oracle10g",
				"d7gis",
				"D7TRADM",
				"gis0526@d7gis.world");
		try{
			conn.open_connection();
			
			_log.info("Start transaction");
			conn.getSeConnection().setTransactionAutoCommit(0);
			conn.getSeConnection().startTransaction();
			
			GtfsSchemaCreationImpl schema;
			
			schema = (GtfsSchemaCreationImpl) factory.getBean("shapes");
			schema = ommitShapeColumn(schema);
			conn.create_table(schema.getFname(), schema.getFields(), schema.getTypes(), schema.getSizes(), schema.getIsNullAble(), null);
			if(schema.getDeletedColumnHeader()!=null){
				conn.create_spatial_column(schema.getFname(), schema.getDeletedColumnHeader(), SeLayer.SE_LINE_TYPE_MASK, "");
			}
			_log.info(schema.getFname());
			
			schema = (GtfsSchemaCreationImpl) factory.getBean("agency");
			schema = ommitShapeColumn(schema);
			conn.create_table(schema.getFname(), schema.getFields(), schema.getTypes(), schema.getSizes(), schema.getIsNullAble(), "DATASET_ID");
			if(schema.getDeletedColumnHeader()!=null){
				conn.create_spatial_column(schema.getFname(), schema.getDeletedColumnHeader(), SeLayer.SE_POINT_TYPE_MASK, "");
			}
			_log.info(schema.getFname());
			
			schema = (GtfsSchemaCreationImpl) factory.getBean("stops");
			schema = ommitShapeColumn(schema);
			conn.create_table(schema.getFname(), schema.getFields(), schema.getTypes(), schema.getSizes(), schema.getIsNullAble(), null);
			if(schema.getDeletedColumnHeader()!=null){
				conn.create_spatial_column(schema.getFname(), schema.getDeletedColumnHeader(), SeLayer.SE_POINT_TYPE_MASK, "");
			}
			_log.info(schema.getFname());
			
			schema = (GtfsSchemaCreationImpl) factory.getBean("routes");
			schema = ommitShapeColumn(schema);
			conn.create_table(schema.getFname(), schema.getFields(), schema.getTypes(), schema.getSizes(), schema.getIsNullAble(), null);
			if(schema.getDeletedColumnHeader()!=null){
				conn.create_spatial_column(schema.getFname(), schema.getDeletedColumnHeader(), SeLayer.SE_POINT_TYPE_MASK, "");
			}
			
			schema = (GtfsSchemaCreationImpl) factory.getBean("trips");
			schema = ommitShapeColumn(schema);
			conn.create_table(schema.getFname(), schema.getFields(), schema.getTypes(), schema.getSizes(), schema.getIsNullAble(), null);
			if(schema.getDeletedColumnHeader()!=null){
				conn.create_spatial_column(schema.getFname(), schema.getDeletedColumnHeader(), SeLayer.SE_POINT_TYPE_MASK, "");
			}
			
			schema = (GtfsSchemaCreationImpl) factory.getBean("stop_times");
			schema = ommitShapeColumn(schema);
			conn.create_table(schema.getFname(), schema.getFields(), schema.getTypes(), schema.getSizes(), schema.getIsNullAble(), null);
			if(schema.getDeletedColumnHeader()!=null){
				conn.create_spatial_column(schema.getFname(), schema.getDeletedColumnHeader(), SeLayer.SE_POINT_TYPE_MASK, "");
			}
			
			schema = (GtfsSchemaCreationImpl) factory.getBean("calendar");
			schema = ommitShapeColumn(schema);
			conn.create_table(schema.getFname(), schema.getFields(), schema.getTypes(), schema.getSizes(), schema.getIsNullAble(), null);
			if(schema.getDeletedColumnHeader()!=null){
				conn.create_spatial_column(schema.getFname(), schema.getDeletedColumnHeader(), SeLayer.SE_POINT_TYPE_MASK, "");
			}
			
			schema = (GtfsSchemaCreationImpl) factory.getBean("calendar_dates");
			schema = ommitShapeColumn(schema);
			conn.create_table(schema.getFname(), schema.getFields(), schema.getTypes(), schema.getSizes(), schema.getIsNullAble(), null);
			if(schema.getDeletedColumnHeader()!=null){
				conn.create_spatial_column(schema.getFname(), schema.getDeletedColumnHeader(), SeLayer.SE_POINT_TYPE_MASK, "");
			}
			
			schema = (GtfsSchemaCreationImpl) factory.getBean("fare_attributes");
			schema = ommitShapeColumn(schema);
			conn.create_table(schema.getFname(), schema.getFields(), schema.getTypes(), schema.getSizes(), schema.getIsNullAble(), null);
			if(schema.getDeletedColumnHeader()!=null){
				conn.create_spatial_column(schema.getFname(), schema.getDeletedColumnHeader(), SeLayer.SE_POINT_TYPE_MASK, "");
			}
			
			schema = (GtfsSchemaCreationImpl) factory.getBean("fare_rules");
			schema = ommitShapeColumn(schema);
			conn.create_table(schema.getFname(), schema.getFields(), schema.getTypes(), schema.getSizes(), schema.getIsNullAble(), null);
			if(schema.getDeletedColumnHeader()!=null){
				conn.create_spatial_column(schema.getFname(), schema.getDeletedColumnHeader(), SeLayer.SE_POINT_TYPE_MASK, "");
			}
			
			schema = (GtfsSchemaCreationImpl) factory.getBean("frequencies");
			schema = ommitShapeColumn(schema);
			conn.create_table(schema.getFname(), schema.getFields(), schema.getTypes(), schema.getSizes(), schema.getIsNullAble(), null);
			if(schema.getDeletedColumnHeader()!=null){
				conn.create_spatial_column(schema.getFname(), schema.getDeletedColumnHeader(), SeLayer.SE_POINT_TYPE_MASK, "");
			}
			
			schema = (GtfsSchemaCreationImpl) factory.getBean("transfers");
			schema = ommitShapeColumn(schema);
			conn.create_table(schema.getFname(), schema.getFields(), schema.getTypes(), schema.getSizes(), schema.getIsNullAble(), null);
			if(schema.getDeletedColumnHeader()!=null){
				conn.create_spatial_column(schema.getFname(), schema.getDeletedColumnHeader(), SeLayer.SE_POINT_TYPE_MASK, "");
			}
			
			_log.info("Commit transaction");
			conn.getSeConnection().commitTransaction();
			
    	} catch (SeException e) {
    		_log.error(e.getMessage());
    		_log.error(e.getSeError().getErrDesc());
    		_log.error(e.getSeError().getSdeErrMsg());
    		e.printStackTrace();
    		try {
    			_log.info("Roll back transaction");
				conn.getSeConnection().rollbackTransaction();
			} catch (SeException e1) {
				// TODO Auto-generated catch block
				_log.error(e1.getMessage());
	    		_log.error(e1.getSeError().getErrDesc());
	    		_log.error(e1.getSeError().getSdeErrMsg());
	    		e1.printStackTrace();
			}
		} finally {
			try{
				conn.close_connection();
			} catch (SeException e){
				_log.error(e.getMessage());
	    		_log.error(e.getSeError().getErrDesc());
	    		_log.error(e.getSeError().getSdeErrMsg());
	    		e.printStackTrace();
			}
		}
    	_log.info("Done");
    	System.out.println("Task completed!");
	}		
	
	/**
	 * Delete the spatial column from the base table so we can invoke spatial column later
	 * @param beforeSchema - Schema with spatial column name
	 * @return New schema without the spatial column
	 */
	private static GtfsSchemaCreationImpl ommitShapeColumn(GtfsSchemaCreationImpl beforeSchema){
		GtfsSchemaCreationImpl afterSchema = new GtfsSchemaCreationImpl(beforeSchema);
		ArrayList<Integer> afterTypes = new ArrayList<Integer>();
		afterTypes.addAll(afterSchema.getTypes());
		
		for(int i=0; i<afterTypes.size(); i++){
			if(afterTypes.get(i) == SeColumnDefinition.TYPE_SHAPE)
				afterSchema.deleteColumn(i);
		}
		return afterSchema;
	}
}