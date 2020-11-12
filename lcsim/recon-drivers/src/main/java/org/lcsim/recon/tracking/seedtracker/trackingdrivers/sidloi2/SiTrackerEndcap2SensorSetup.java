package org.lcsim.recon.tracking.seedtracker.trackingdrivers.sidloi2;

import org.lcsim.recon.tracking.digitization.sisim.config.*;
import hep.physics.matrix.BasicMatrix;
import hep.physics.vec.BasicHep3Vector;
import hep.physics.vec.VecOp;

import java.util.List;

import org.lcsim.detector.IDetectorElement;
import org.lcsim.detector.IRotation3D;
import org.lcsim.detector.ITranslation3D;
import org.lcsim.detector.RotationPassiveXYZ;
import org.lcsim.detector.Transform3D;
import org.lcsim.detector.Translation3D;
import org.lcsim.detector.solids.Polygon3D;
import org.lcsim.detector.solids.Trd;
import org.lcsim.detector.tracker.silicon.ChargeCarrier;
import org.lcsim.detector.tracker.silicon.SiSensor;
import org.lcsim.detector.tracker.silicon.SiSensorElectrodes;
import org.lcsim.detector.tracker.silicon.SiStrips;
import org.lcsim.geometry.Detector;
import org.lcsim.geometry.compact.Subdetector;
import org.lcsim.geometry.subdetector.SiTrackerEndcap2;
import org.lcsim.util.Driver;

public class SiTrackerEndcap2SensorSetup extends Driver 
{
	String subdetectorName;
	double readoutPitch = 0.050;
	double sensePitch = 0.025;
	double transferEfficiencies[] = {0.986,0.419};

	public SiTrackerEndcap2SensorSetup()
	{}

	public SiTrackerEndcap2SensorSetup(String subdetectorName)
	{
		this.subdetectorName = subdetectorName;
	}

	public void setSubdetectorName(String subdetectorName)
	{
		this.subdetectorName = subdetectorName;
	}

	public void detectorChanged(Detector detector)
	{
		if (subdetectorName == null)
			throw new RuntimeException("The subdetectorName was not set.");

		Subdetector subdetector = detector.getSubdetector(subdetectorName);
		if (subdetector instanceof SiTrackerEndcap2)
			setupSensorDetectorElements(subdetector);
		else
			throw new RuntimeException("The subdetector " + subdetectorName + " is not an instance of SiTrackerEndcap.");
	}
	
	public void setReadoutPitch(double p)
	{
		this.readoutPitch = p;
	}
	
	public void setSensePitch(double p)
	{
		this.sensePitch = p;
	}
	
	public void setTransferEfficiencies(double transferEfficiencies[])
	{
		if (transferEfficiencies.length < 2)
		{
			throw new IllegalArgumentException("Not enough values in transferEfficiencies array.");
		}
		this.transferEfficiencies[0] = transferEfficiencies[0];
		this.transferEfficiencies[1] = transferEfficiencies[1];
	}

	private void setupSensorDetectorElements(Subdetector subdet)
	{
		for ( IDetectorElement endcap : subdet.getDetectorElement().getChildren() )
		{
			for ( IDetectorElement layer : endcap.getChildren() )
			{
				for ( IDetectorElement wedge : layer.getChildren() )
				{
					for ( IDetectorElement module : wedge.getChildren() )
					{
						List<SiSensor> sensors = module.findDescendants(SiSensor.class);

						if (sensors.size() == 0)
							throw new RuntimeException("No sensors found in module.");

						for (SiSensor sensor : sensors)
						{
							Trd sensor_solid = (Trd)sensor.getGeometry().getLogicalVolume().getSolid();
                            
                            Polygon3D inside = sensor_solid.getFacesNormalTo(new BasicHep3Vector(0,-1,0)).get(0);
                            Polygon3D outside = sensor_solid.getFacesNormalTo(new BasicHep3Vector(0,1,0)).get(0);

                            Polygon3D n_side = inside;
                            Polygon3D p_side = outside;

                            // Bias the sensor
                            sensor.setBiasSurface(ChargeCarrier.HOLE,p_side);
                            sensor.setBiasSurface(ChargeCarrier.ELECTRON,n_side);
                                                         
                            double strip_angle = Math.atan2(sensor_solid.getXHalfLength2() - sensor_solid.getXHalfLength1(), sensor_solid.getZHalfLength() * 2);
                                                        
                            ITranslation3D electrodes_position = new Translation3D(VecOp.mult(-p_side.getDistance(),new BasicHep3Vector(0,0,1)));  // translate to outside of polygon
                            IRotation3D electrodes_rotation = new RotationPassiveXYZ(-Math.PI/2,0,strip_angle);
                            Transform3D electrodes_transform = new Transform3D(electrodes_position, electrodes_rotation);                                
                            
                            // Free calculation of readout electrodes, sense electrodes determined thereon
                            SiSensorElectrodes readout_electrodes = new SiStrips(ChargeCarrier.HOLE,this.readoutPitch,sensor,electrodes_transform);
                            SiSensorElectrodes sense_electrodes = new SiStrips(ChargeCarrier.HOLE,this.sensePitch,(readout_electrodes.getNCells()*2-1),sensor,electrodes_transform);                                                                
                            
                            sensor.setSenseElectrodes(sense_electrodes);
                            sensor.setReadoutElectrodes(readout_electrodes);
                            
                            double[][] transfer_efficiencies = { transferEfficiencies };
                            sensor.setTransferEfficiencies(ChargeCarrier.HOLE,new BasicMatrix(transfer_efficiencies));
						}
					}
				}                
			}        
		}
	}
}


