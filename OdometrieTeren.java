package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

public class OdometrieTeren extends OdometrieRobot {

    /// Coordonatele fixe ale coșului pe teren (în cm)
    public static double POZITIE_COS_X = 152.4;
    public static double POZITIE_COS_Y = 182.88;

    public OdometrieTeren(HardwareMap hardwareMap, IMU imuExistent, String numePodParalel, String numePodPerpendicular) {
        super(hardwareMap, imuExistent, numePodParalel, numePodPerpendicular);
    }

      ///Calculez puterea de rotație (rx) necesară pentru ca robotul să se rotească automat cu fața către coș.
    public double calculeazaRxSpreCos() {
        double curentX = getX();
        double curentY = getY();

        if( curentY >= 182.5) {
            POZITIE_COS_X = 213.3;
        }
        else {
            POZITIE_COS_X = 152.4;
        }
        double x = curentX - POZITIE_COS_X;
        double y = curentY - POZITIE_COS_Y;

        return Math.toDegrees(Math.atan2(x,y));
    }
      ///Calculează distanța directă de la robot până la coș (folositoare pentru viteza aruncării)
    public double getDistantaPanaLaCos() {
        double deltaX = POZITIE_COS_X - getX();///distanta de la cos pana la robot X
        double deltaY = POZITIE_COS_Y - getY();///distanta de la cos pana la robot Y
        return Math.hypot(deltaX, deltaY);
    }
}

