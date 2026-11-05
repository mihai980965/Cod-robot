package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

public class Tureta extends OdometrieTeren{
    private Servo tr;

    public Tureta(HardwareMap hardwareMap, IMU imuExistent, String numePodParalel, String numePodPerpendicular) {
        super(hardwareMap, imuExistent, numePodParalel, numePodPerpendicular);
        tr = hardwareMap.get(Servo.class, "Tureta");
    }
    public void rotatie(){
        double angleTureta = (tr.getPosition() - 0.5) * 180.0d;
        double angleTurSut = calculeazaRxSpreCos();
        double eroareAngle = angleTurSut - angleTureta;

        tr.setPosition(eroareAngle / 180.0d);
    }
}
