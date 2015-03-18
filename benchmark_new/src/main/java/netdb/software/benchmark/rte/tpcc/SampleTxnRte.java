package netdb.software.benchmark.rte.tpcc;

import netdb.software.benchmark.TxnResultSet;
import netdb.software.benchmark.rte.RemoteTerminalEmulator;
import netdb.software.benchmark.rte.executor.SampleTxnExecutor;
import netdb.software.benchmark.rte.executor.TransactionExecutor;
import netdb.software.benchmark.rte.txparamgen.SampleTxnParamGen;

public class SampleTxnRte extends RemoteTerminalEmulator {

	private SampleTxnParamGen paramGem;

	public SampleTxnRte(Object[] connArgs) {
		super(connArgs);
		paramGem = new SampleTxnParamGen();
	}

	@Override
	protected TxnResultSet executeTxnCycle() {
		TransactionExecutor tx = new SampleTxnExecutor(paramGem);
		return tx.execute(conn);
	}

}
