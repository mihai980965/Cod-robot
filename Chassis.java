package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name = "Mecanum Chassis", group = "TeleOp")
public class Chassis extends LinearOpMode {
    private DcMotorEx fl, fr, bl, br; /// motoarele de la sasiu

    @Override
    public void runOpMode() throws InterruptedException {
        /// hardwaremap este folosit ca sa stii cum se numeste in aplicatie motorul
        fl = hardwareMap.get(DcMotorEx.class, "Frontleft");
        fr = hardwareMap.get(DcMotorEx.class, "Frontright");
        bl = hardwareMap.get(DcMotorEx.class, "Backleft");
        br = hardwareMap.get(DcMotorEx.class, "Backright");

        /// prin Direction arati in ce directie sa se miste motorul
        fl.setDirection(DcMotorSimple.Direction.REVERSE);
        bl.setDirection(DcMotorSimple.Direction.REVERSE);
        fr.setDirection(DcMotorSimple.Direction.FORWARD);
        br.setDirection(DcMotorSimple.Direction.FORWARD);

        /// ZeroPowerBehavior inseamna ca motoarele se opresc automat cand nu mai dai putere
        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        /// dezactivezi limita de tensiuni din control hub de la baterie
        fl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        fr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        br.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        /// apeleaza senzorul IMU pentru orientare
        IMU imu = hardwareMap.get(IMU.class, "imu");
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));
        imu.initialize(parameters);

        /// facem instante pentru intake si storage
        Intake intake = new Intake(hardwareMap);
        Transfer storage = new Transfer(hardwareMap);

        /// wait for start se pune neaparat pentru a incepe rularea codului
        waitForStart();

        while (opModeIsActive()) {
            /// initializezi joystick-urile si triggerele pentru deplasare si rotatie
            double rawY = -gamepad1.left_stick_y;
            double rawX = gamepad1.left_stick_x;
            double rawRx = gamepad1.right_trigger - gamepad1.left_trigger;

            /// deadband pentru a preveni Stick Drift
            double y = applyDeadband(rawY, 0.05);
            double x = applyDeadband(rawX, 0.05);
            double rx = applyDeadband(rawRx, 0.05);

            /// curba cubica pentru control de mare precizie
            y = Math.pow(y, 3);
            x = Math.pow(x, 3);
            rx = Math.pow(rx, 3);

            /// Reseteaza orientarea (Heading) la butonul Options
            if (gamepad1.options) {
                imu.resetYaw();
            }

            /// formula matricei de rotație 2D pentru Field-Centric
            double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
            double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
            double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

            /// impuls suplimentar pe mișcarea laterală
            rotX = rotX * 1.1;

            /// calculare și normalizare puteri motoare mecanum
            double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1.0);
            double flp = (rotY + rotX + rx) / denominator;
            double blp = (rotY - rotX + rx) / denominator;
            double frp = (rotY - rotX - rx) / denominator;
            double brp = (rotY + rotX - rx) / denominator;

            /// trimitem puterile la motoare
            fl.setPower(flp);
            bl.setPower(blp);
            fr.setPower(frp);
            br.setPower(brp);

            /// actualizeaza intake si storage
            intake.update(gamepad1);
            storage.update(gamepad1);
        }
    }

    /// STICK DRIFT
    private double applyDeadband(double value, double threshold) {
        if (Math.abs(value) < threshold) {
            return 0.0;
        }
        return value;
    }
}
