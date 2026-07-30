package org.firstinspires.ftc.teamcode.main;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

//this is a comment

@TeleOp
public class teleop extends LinearOpMode {

    private Follower follower;
    private intake   intake;
    private shooter  shooter;
    private turret   turret;
    private Servo    rgb, hood;
    private DcMotor  fl, bl, fr, br;

    private boolean lastBackState = false;
    private final Pose startPose  = new Pose(72, 72, Math.toRadians(90));

    @Override
    public void runOpMode() {
        // Pedro Pathing must initialize first — it touches the I2C bus and
        // can disrupt the Limelight if it initializes after it.
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startPose);

        fl = hardwareMap.dcMotor.get("fl");
        bl = hardwareMap.dcMotor.get("bl");
        fr = hardwareMap.dcMotor.get("fr");
        br = hardwareMap.dcMotor.get("br");

        br.setDirection(DcMotorSimple.Direction.REVERSE);
        fr.setDirection(DcMotorSimple.Direction.REVERSE);

        intake  = new intake(hardwareMap);
        shooter = new shooter(hardwareMap);

        // Turret and Limelight initialize last, after Pedro has settled
        turret  = new turret(hardwareMap, startPose);
        turret.init();

        if (isStopRequested()) return;

        waitForStart();

        while (opModeIsActive()) {
            follower.update();
            Pose currentPose = follower.getPose();

            // ── Drivetrain ────────────────────────────────────────────────────
            double y  = -gamepad1.left_stick_y;
            double x  =  gamepad1.left_stick_x;
            double rx =  gamepad1.right_stick_x;

            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            fl.setPower((y + x + rx) / denominator);
            bl.setPower((y - x + rx) / denominator);
            fr.setPower((y - x - rx) / denominator);
            br.setPower((y + x - rx) / denominator);

            // ── Turret toggle (right bumper, rising edge) ─────────────────────
            boolean bumperPressed = gamepad1.right_bumper;
            if (bumperPressed && !lastBackState) turret.toggle();
            lastBackState = bumperPressed;
            turret.update(currentPose);

            // ── Intake & Shooter ──────────────────────────────────────────────
            intake.update(gamepad1.left_trigger > 0.3);
            shooter.update(gamepad1.left_bumper, gamepad1.y, currentPose);

            // ── Pose reset ────────────────────────────────────────────────────
            if (gamepad1.x) {
                follower.setPose(new Pose(14, 80, Math.toRadians(180)));
            }

            // ── Telemetry ─────────────────────────────────────────────────────

            // Shooter
            telemetry.addLine("=== SHOOTER ===");
            telemetry.addData("Shooter",         shooter.isEnabled() ? "ON" : "OFF");
            telemetry.addData("Gate",             shooter.isGateOpen() ? "OPEN" : "CLOSED");
            telemetry.addData("Target Velocity",  String.format("%.0f ticks/s", shooter.getTargetVelocity()));
            telemetry.addData("fwL Velocity",     String.format("%.0f ticks/s", shooter.getLeftVelocity()));
            telemetry.addData("fwR Velocity",     String.format("%.0f ticks/s", shooter.getRightVelocity()));
            telemetry.addData("Shooter Ready",    shooter.isReady() ? "YES" : "NO");
            telemetry.addData("Distance to Goal", String.format("%.1f in", shooter.getDistance()));

            // Turret
            telemetry.addLine("");
            telemetry.addLine("=== TURRET ===");
            telemetry.addData("Turret",           turret.isTracking() ? "ON" : "OFF");
            telemetry.addData("Tracking Mode",    turret.isUsingVision() ? "VISION (Limelight)" : "ODOMETRY (Pedro)");
            telemetry.addData("Current Pos",      String.format("%.0f ticks", turret.getCurrentPositionTicks()));
            telemetry.addData("Target Pos",       String.format("%.0f ticks", turret.getTargetTicks()));
            telemetry.addData("Error",            String.format("%.0f ticks", turret.getErrorTicks()));
            telemetry.addData("Turret Ready",     Math.abs(turret.getErrorTicks()) < 20 ? "YES" : "NO");
            if (turret.getRawTx() != null) {
                telemetry.addData("Limelight tx", String.format("%.2f°", turret.getRawTx()));
            }

            // Odometry
            telemetry.addLine("");
            telemetry.addLine("=== POSITION ===");
            telemetry.addData("X",       String.format("%.2f in", currentPose.getX()));
            telemetry.addData("Y",       String.format("%.2f in", currentPose.getY()));
            telemetry.addData("Heading", String.format("%.1f°", Math.toDegrees(currentPose.getHeading())));

            // Intake
            telemetry.addLine("");
            telemetry.addLine("=== INTAKE ===");
            telemetry.addData("Intake", intake.isIntakeOn() ? "ON" : "OFF");

            telemetry.addLine("");
            telemetry.addLine("LB → shooter  |  Y → gate  |  RB → turret  |  LT → intake  |  X → reset pose");
            telemetry.update();
        }

        turret.stop();
    }
}
