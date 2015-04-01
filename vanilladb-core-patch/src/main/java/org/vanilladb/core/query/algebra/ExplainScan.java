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
		System.out.println("Status in EXPSCAN = " + status);
		if (status == 0) {
			status++;
			return s.next();
		}
		else if (status == 1) {
			status++;

			recsCt = 0;
			while (s.next()) {
				recsCt++;
			}
			return true;
		}
		else {
			status++;
			return s.next();
		}
	}

	@Override
	public void close() {
		s.close();
	}

	@Override
	public Constant getVal(String fldName) {
		if (status < 2) {
			String ret = "\nStatus: " + status + explainResult
						+ "\n\n" + "Actual #recs: " + recsCt;
			return Constant.newInstance(Type.VARCHAR(500), ret.getBytes());
		} else {
			String ret = "Actual #recs: " + recsCt;
			return Constant.newInstance(Type.VARCHAR(500), ret.getBytes());
		}
	}

	@Override
	public boolean hasField(String fldName) {
		return fieldList.contains(fldName);
	}
}
