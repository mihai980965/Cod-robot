package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Shooter {

    private DcMotorEx mt; /// Motorul de la tureta (Flywheel)
    private OdometrieTeren odometrie;

    public Shooter(HardwareMap hardwareMap, OdometrieTeren odometrieTeren) {
        this.odometrie = odometrieTeren;
        this.mt = hardwareMap.get(DcMotorEx.class, "MotorTureta");

        /// Folosim RUN_USING_ENCODER pentru o viteză constantă indiferent de bateria rămasă
        this.mt.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /// Calculează și aplică viteza necesară flywheel-ului bazat pe distanța calculată din od6ometrie.
    public double calculeazaSiAplicaVitezaFlyWheel() {
        double distantaMeti = odometrie.getDistantaPanaLaCos() / 100.0;/// Obținem distanța în metri (de la cm la m)

        double g = 9.81;
        double unghiGrade = 55.0; /// Unghiul rampei de lansare
        double unghiRad = Math.toRadians(unghiGrade);

        double inaltimeCosM = 1.66;   /// Înălțimea coșului în metri
        double inaltimeRobotM = 0.35; /// Înălțimea la care e montat shooterul
        double deltaY = inaltimeCosM - inaltimeRobotM;

        double razaRotiLansatoareM = 0.0508; /// Raza flywheel-ului în metri (~2 inci)
        double eficientaTransfer = 0.55;    /// Coeficient de pierdere la frecare

        double numitor = (distantaMeti * Math.tan(unghiRad)) - deltaY;
        if (numitor <= 0) {
            mt.setPower(0.0);
            return 0.0;
        }

        /// Formula pentru viteza inițială a mingii (v0)
        double v0 = Math.sqrt((g * Math.pow(distantaMeti, 2)) / (2 * Math.pow(Math.cos(unghiRad), 2) * numitor));
        
        /// Viteza necesară la suprafața roții flywheel-ului
        double vwheel = v0 / eficientaTransfer;
        
        /// Viteza în RPM (rotații pe minut)
        double rpm = (vwheel * 60.0) / (2 * Math.PI * razaRotiLansatoareM);

        /// Convertim RPM în putere între 0.0 și 1.0 (raportat la un RPM maxim al motorului de ex: 6000 RPM)
        double putere = rpm / 6000.0;
        if (putere > 1.0) putere = 1.0;
        if (putere < 0.0) putere = 0.0;

        mt.setPower(putere);
        return rpm;
    }

    public void opreste() {

        mt.setPower(0.0);
    }
}
