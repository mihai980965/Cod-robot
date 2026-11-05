package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Transfer {

    private DcMotorEx storageM1;///declararea motorului

    public Transfer(HardwareMap hardwareMap) {
        storageM1 = hardwareMap.get(DcMotorEx.class, "StorageM1");///declari motorul in map
        storageM1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);///setezi sa se opreasca automat motorul
        storageM1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);///scoti limita de viteza
    }
    public void urca() {/// dai putere motoarelor ca sa se roteasca motorul

        storageM1.setPower(1.0);
    }
    public void coboara() {/// dai invers putere motorului

        storageM1.setPower(-1.0);
    }
    public void stop() {/// opresti motorul

        storageM1.setPower(0.0);
    }
    public void update(Gamepad gamepad) {/// dai update la toate functiile de sus
        if (gamepad.right_bumper) {
            urca();
        }
        else if (gamepad.left_bumper) {
            coboara();
        }
        else {
            stop();
        }
    }
}
