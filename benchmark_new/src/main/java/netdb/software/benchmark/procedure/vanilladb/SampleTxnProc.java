package netdb.software.benchmark.procedure.vanilladb;

import java.sql.Connection;

import netdb.software.benchmark.procedure.SampleTxnProcParamHelper;

import org.vanilladb.core.query.algebra.Plan;
import org.vanilladb.core.query.algebra.Scan;
import org.vanilladb.core.remote.storedprocedure.SpResultSet;
import org.vanilladb.core.server.VanillaDb;
import org.vanilladb.core.sql.storedprocedure.StoredProcedure;
import org.vanilladb.core.storage.tx.Transaction;

public class SampleTxnProc implements StoredProcedure {

	private SampleTxnProcParamHelper paramHelper = new SampleTxnProcParamHelper();
	private Transaction tx;

	public SampleTxnProc() {

	}

	@Override
	public void prepare(Object... pars) {
		paramHelper.prepareParameters(pars);
	}

	@Override
	public SpResultSet execute() {
		tx = VanillaDb.txMgr().transaction(Connection.TRANSACTION_SERIALIZABLE,
				false);
		try {
			executeSql();
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			paramHelper.setCommitted(false);
			e.printStackTrace();
		}
		return paramHelper.createResultSet();
	}

	protected void executeSql() {
		String name = "";

		String sql = "SELECT i_name FROM item WHERE i_id = "
				+ paramHelper.getItemId();
		Plan p = VanillaDb.newPlanner().createQueryPlan(sql, tx);
		Scan s = p.open();
		s.beforeFirst();
		if (s.next()) {
			name = (String) s.getVal("i_name").asJavaVal();
		} else
			throw new RuntimeException();
		
		paramHelper.setItemName(name);
		
		s.close();
	}
}
