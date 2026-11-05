package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class OdometrieRobot {
    private DcMotorEx podParalel;///odmetria pe sensul Y
    private DcMotorEx podPerpendicular;///odometria pe sensul X
    private IMU imu;

    public static final double DIAMETRU_ROATA_CM = 3.5;
    public static final double TIKS_PER_ROTATIE = 2000.0;
    public static final double OFFSET_PERPENDICULAR_CM = 10.0;

    private final double CM_PER_TICK = (Math.PI * DIAMETRU_ROATA_CM) / TIKS_PER_ROTATIE;

    public static double robotX = 0;///setezi pozitia initiala x a robotului 0
    public static double robotY = 0;///setezi pozitia initiala y a robotului 0
    private double robotHeading = 0;///orientarea robotului, cum se uita el

    private int lastTiksParalel = 0;///Memorează numărul de ticks ale podului paralel (Y) de la ultimul update()
    private int lastTiksPerpendicular = 0;///Memorează numărul de ticks ale podului paralel (x) de la ultimul update()
    private double lastHeading = 0;///Memorează orientarea/unghiul robotului de la IMU (în radiani) de la ultimul update()

    /// asta este un constructor, ca sa poti face o instanta,dar nu e neaparat, 
    /// doar daca vrei sa ai ceva in constructor,pentru ca java iti creeaza automat
    /// un constructor gol cand faci o instanta
    public OdometrieRobot(HardwareMap hardwareMap, IMU imuExistent, String numePodParalel, String numePodPerpendicular){
        this.podParalel = hardwareMap.get(DcMotorEx.class, numePodParalel);
        this.podPerpendicular = hardwareMap.get(DcMotorEx.class, numePodPerpendicular);
        this.imu = imuExistent;

        resetEncodere();
    }
    /// un constructor gol secundar pentru OdometrieTeren
    public OdometrieRobot() {
    }

    public void resetEncodere(){/// Salvează starea curentă a senzorilor ca punct de plecare
        lastTiksParalel = podParalel.getCurrentPosition();
        lastTiksPerpendicular = podPerpendicular.getCurrentPosition();
        if(imu != null){
            lastHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
        }
    }
    public void update(){
        /// Citirea valorilor curente de la senzori
        int currentTiksParalel = podParalel.getCurrentPosition();
        int currentTiksPerpendicular = podPerpendicular.getCurrentPosition();
        double currentHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
        /// Calcularea diferențelor de mișcare
        double deltaParalel = (currentTiksParalel - lastTiksParalel) * CM_PER_TICK;
        double deltaPerpendicularRaw = (currentTiksPerpendicular - lastTiksPerpendicular) * CM_PER_TICK;
        double deltaHeading = currentHeading -lastHeading;
        ///  Corecția rotației pentru podul perpendicular
        double deltaPerpendicular = deltaPerpendicularRaw - (deltaHeading * OFFSET_PERPENDICULAR_CM);
        /// Folosește unghiul de la jumătatea mișcării pentru a aproxima traiectoria ca o linie dreaptă scurta
        double unghiMediu = lastHeading + (deltaHeading / 2.0);
        /// Transformarea in Coordonate Globale pe Teren
        double deltaXGlobal = deltaPerpendicular * Math.cos(unghiMediu) - deltaParalel * Math.sin(unghiMediu);
        double deltaYGlobal = deltaPerpendicular * Math.sin(unghiMediu) + deltaParalel * Math.cos(unghiMediu);
        /// Actualizarea poziției totale și salvarea stării
        robotX += deltaXGlobal;
        robotY += deltaYGlobal;
        robotHeading = currentHeading;
        lastTiksParalel = currentTiksParalel;
        lastTiksPerpendicular = currentTiksPerpendicular;
        lastHeading = currentHeading;
    }
    public double getX(){/// astea sunt getere(adica niste metode de acces)

        return robotX;
    }
    public double getY() {

        return robotY;
    }
    public double getHeadingGrade(){

        return Math.toDegrees(robotHeading);
    }

    public double getHeadingRadiani(){

        return robotHeading;
    }
}
