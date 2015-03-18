package netdb.software.benchmark.procedure;

import org.vanilladb.core.remote.storedprocedure.SpResultSet;
import org.vanilladb.core.sql.Schema;
import org.vanilladb.core.sql.Type;
import org.vanilladb.core.sql.VarcharConstant;
import org.vanilladb.core.sql.storedprocedure.SpResultRecord;
import org.vanilladb.core.sql.storedprocedure.StoredProcedureParamHelper;

public class SampleTxnProcParamHelper extends StoredProcedureParamHelper {

	private int itemId;
	private String itemName;

	public int getItemId(){
		return itemId;
	}
	
	public  void setItemName(String s){
		itemName = s;
	}

	@Override
	public void prepareParameters(Object... pars) {
		itemId = (Integer) pars[0];
	}

	@Override
	public SpResultSet createResultSet() {
		/*
		 * TODO The output information is not strictly followed the TPC-C
		 * definition. See the session 2.4.3.5 in TPC-C 5.11 document.
		 */
		Schema sch = new Schema();
		Type statusType = Type.VARCHAR(10);
		Type itemNameType = Type.VARCHAR(24);
		sch.addField("status", statusType);
		sch.addField("item_name", itemNameType);

		SpResultRecord rec = new SpResultRecord();
		String status = isCommitted ? "committed" : "abort";
		rec.setVal("status", new VarcharConstant(status, statusType));
		rec.setVal("item_name", new VarcharConstant(itemName, itemNameType));

		return new SpResultSet(sch, rec);
	}

}
