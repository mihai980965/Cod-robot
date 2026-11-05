package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {

    private DcMotorEx IntakeM1,IntakeM2;///declararea motoarelor de la intake

    public Intake(HardwareMap hardwareMap) {
        /// declari motoarele in map
        IntakeM1 = hardwareMap.get(DcMotorEx.class, "IntakeM1");
        IntakeM2 = hardwareMap.get(DcMotorEx.class, "IntakeM2");
        /// setezi motoarele sa se opreasca automat, nu din inertie
        IntakeM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        IntakeM2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        /// scoti limita de viteza de la control hub
        IntakeM1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        IntakeM2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void collect() {/// luare mingilor prin intake

        IntakeM1.setPower(1.0);
        IntakeM2.setPower(1.0);
    }

    public void eject() {///  eject la mingi din storage

        IntakeM1.setPower(-1.0);
        IntakeM2.setPower(-1.0);
    }

    public void stop() {/// atunci cand nu faci nimica motoarele se opresc

        IntakeM1.setPower(0.0);
        IntakeM2.setPower(0.0);
    }

    public void update(Gamepad gamepad) {/// updateaza comenzile, atunci cand ai apasat pe butoane
        if (gamepad.right_bumper) {
            collect();
        } else if (gamepad.left_bumper) {
            eject();
        } else {
            stop();
        }
    }
}
