package com.missionsky.scp.dataanalysis.algorithm.dataaggregate;



import com.missionsky.scp.dataanalysis.algorithm.BasicAlgorithm;
import com.missionsky.scp.dataanalysis.entity.AlgorithmInfoAnnotation;
import com.missionsky.scp.dataanalysis.entity.AlgorithmType;

/**
 * Duplicate Removal Algorithm  abstract class
 * @author ellis.xie 
 * @version 1.0
 */

@AlgorithmInfoAnnotation(name="DuplicateRemovalAlgorithm", algorithmType=AlgorithmType.BasicAlgothm, description="DuplicateRemoval aggregate algorithm")

public abstract class DuplicateRemovalAlgorithm extends BasicAlgorithm {

}
