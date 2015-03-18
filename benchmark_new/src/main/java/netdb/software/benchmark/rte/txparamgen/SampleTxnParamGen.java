package netdb.software.benchmark.rte.txparamgen;

import java.util.LinkedList;

import netdb.software.benchmark.TpccConstants;
import netdb.software.benchmark.TransactionType;
import netdb.software.benchmark.util.RandomValueGenerator;

public class SampleTxnParamGen implements TxParamGenerator {

	public SampleTxnParamGen() {

	}

	@Override
	public TransactionType getTxnType() {
		return TransactionType.SAMPLE_TXN;
	}


	@Override
	public Object[] generateParameter() {
		RandomValueGenerator rvg = new RandomValueGenerator();
		LinkedList<Object> paramList = new LinkedList<Object>();

		paramList.add(rvg.number(1, TpccConstants.NUM_ITEMS));

		return paramList.toArray();
	}

}
