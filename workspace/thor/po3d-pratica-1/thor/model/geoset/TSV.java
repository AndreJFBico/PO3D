package thor.model.geoset;

import java.util.ArrayList;
import java.util.List;

public class TSV {
	double _S;
	List<TSV> _nextTSVs = new ArrayList<TSV>();
	
	public TSV(double S)
	{
		_S = S;
	}
}
