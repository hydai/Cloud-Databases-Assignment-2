package org.vanilladb.core.query.algebra;

import java.util.Collection;

import org.vanilladb.core.sql.Constant;
import org.vanilladb.core.sql.Type;

public class ExplainScan implements Scan {
	private Scan s;
	private Collection<String> fieldList;
	private String explainResult;
	private int status = 0;
	private int recsCt = 0;
	public ExplainScan(Scan s, Collection<String> fieldList) {
		this.s = s;
		this.fieldList = fieldList;
		explainResult = "";
	}

	public void setExplainResult(String eR) {
		this.explainResult = eR;
	}
	
	@Override
	public void beforeFirst() {
		s.beforeFirst();
	}

	@Override
	public boolean next() {
		if (status == 0) {
			while (s.next()) {
				recsCt++;
			}
			status++;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void close() {
		s.close();
	}

	@Override
	public Constant getVal(String fldName) {
		String ret = explainResult + "\n" + "Actual #recs: " + recsCt;
		return Constant.newInstance(Type.VARCHAR(500), ret.getBytes());	
	}

	@Override
	public boolean hasField(String fldName) {
		return fieldList.contains(fldName);
	}
}
