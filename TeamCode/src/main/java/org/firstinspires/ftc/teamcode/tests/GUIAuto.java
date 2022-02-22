package org.firstinspires.ftc.teamcode.tests;

import android.text.Html;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.RoadRunner.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.classes.Arm;
import org.firstinspires.ftc.teamcode.classes.DuckDetectorPipelineBlue;

@Autonomous
public class GUIAuto extends LinearOpMode {
    public void runOpMode(){
        DuckDetectorPipelineBlue.DuckPos duckPos;

        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        Arm arm = new Arm(hardwareMap);
        DuckDetectorPipelineBlue detector = new DuckDetectorPipelineBlue(hardwareMap, "Webcam 1");
        DcMotor intake = hardwareMap.get(DcMotor.class, "intake");
        DcMotorEx duckWheel = hardwareMap.get(DcMotorEx.class, "Rim");


        boolean warehouseAuto = true, warehousePark = true;
        boolean toggle1 = false, toggle2 = false, toggle3 = false, toggle4 = false;
        int c = 0;

        drive.setPoseEstimate(new Pose2d(6.25, 64.88, Math.toRadians(270)));

        while(!gamepad1.right_stick_button && !isStarted()) {
            Pose2d estimate = drive.getPoseEstimate();

            if (gamepad1.dpad_down) {
                if (toggle1) {
                    c++;
                    toggle1 = false;
                }
            } else if (gamepad1.dpad_up) {
                if (toggle1) {
                    c--;
                    toggle1 = false;
                }
            } else {
                toggle1 = true;
            }

            if (c == 0) {
                if (gamepad1.b) {
                    if (toggle2) {
                        warehouseAuto = !warehouseAuto;

                        if (warehouseAuto) {
                            drive.setPoseEstimate(new Pose2d(6.25, 64.88, Math.toRadians(270)));
                            warehousePark = true;
                        } else {
                            drive.setPoseEstimate(new Pose2d(-41.38, 65.75, Math.toRadians(180)));
                            warehousePark = false;
                        }

                        toggle2 = false;
                    }
                } else {
                    toggle2 = true;
                }
            }

            if (c == 1) {
                if (gamepad1.y) {
                    if (toggle3) {
                        drive.setPoseEstimate(new Pose2d(estimate.getX(), (estimate.getY() - 1)));
                        toggle3 = false;
                    }
                } else if (gamepad1.a) {
                    if (toggle3) {
                        drive.setPoseEstimate(new Pose2d(estimate.getX(), (estimate.getY() + 1)));
                        toggle3 = false;
                    }
                } else if (gamepad1.b) {
                    if (toggle3) {
                        drive.setPoseEstimate(new Pose2d((estimate.getX() - 1), estimate.getY()));
                        toggle3 = false;
                    }
                } else if (gamepad1.x) {
                    if (toggle3) {
                        drive.setPoseEstimate(new Pose2d((estimate.getX() + 1), estimate.getY()));
                        toggle3 = false;
                    }
                } else {
                    toggle3 = true;
                }
            }

            if (c == 2 && !warehouseAuto) {
                if (gamepad1.b) {
                    if (toggle4) {
                        warehousePark = !warehousePark;
                        toggle4 = false;
                    }
                } else {
                    toggle4 = true;
                }
            }

            if (warehouseAuto) {
                if (c > 1) {
                    c = 0;
                } else if (c < 0) {
                    c = 1;
                }
            } else {
                if (c > 2) {
                    c = 0;
                } else if (c < 0) {
                    c = 2;
                }
            }

            telemetry.addLine(((c == 0) ? "--->" : "") + "Robot will commence " + (warehouseAuto ? "warehouse auto" : "duck auto"));
            telemetry.addData(((c == 1) ? "--->" : "") + "Robot starting position", "X: " + drive.getPoseEstimate().getX() + "\nY: " + drive.getPoseEstimate().getY());
            if (!warehouseAuto) {
                telemetry.addLine(((c == 2) ? "--->" : "") + "Robot will park in " + (warehousePark ? "the warehouse" : "the storage unit"));
            }
            telemetry.update();
        }

            Trajectory bottomW = drive.trajectoryBuilder(drive.getPoseEstimate())
                    .lineToLinearHeading(new Pose2d(1.75, 44.93, Math.toRadians(56.35)))
                    .addTemporalMarker(1.5, () -> {
                        arm.moveArm(-175,1);
                    })
                    .build();

            Trajectory middleW = drive.trajectoryBuilder(drive.getPoseEstimate())
                    .lineToLinearHeading(new Pose2d(1.62, 45.66, Math.toRadians(56.35)))
                    .addTemporalMarker(.5, () ->{
                        arm.open(.25, 1);
                    })
                    .addTemporalMarker(1.5, () -> {
                        arm.moveArm(-250,1);
                    })
                    .addTemporalMarker(2, () ->{
                        arm.open();
                    })
                    .build();

            Trajectory topW = drive.trajectoryBuilder(drive.getPoseEstimate())
                    .lineToLinearHeading(new Pose2d(-1.89, 39.34, Math.toRadians(52.32)))
                    .addTemporalMarker(1.5, () -> {
                        arm.moveArm(-350,1);
                    })
                    .build();

        while(!isStarted()) {
            telemetry.addLine("FINAL RESULTS:");
            telemetry.addLine("Robot will commence " + (warehouseAuto ? "warehouse auto" : "duck auto"));
            telemetry.addData("Robot starting position", "X: " + drive.getPoseEstimate().getX() + "\nY: " + drive.getPoseEstimate().getY());
            telemetry.addLine("Robot will park in " + (warehousePark ? "the warehouse" : "the storage unit"));
            telemetry.addLine("VISION TELEMETRY:");
            telemetry.addLine("" + detector);
            telemetry.addData("guess", detector.getPosition());
            telemetry.update();
        }

        duckPos = detector.getPosition();

        waitForStart();

        if(warehouseAuto){
            if(duckPos.equals(DuckDetectorPipelineBlue.DuckPos.LEFT)) {
                drive.followTrajectory(bottomW);
                arm.open();
                sleep(250);
                arm.close();
                sleep(250);
            }else if(duckPos.equals(DuckDetectorPipelineBlue.DuckPos.CENTER)){
                drive.followTrajectory(middleW);
                sleep(250);
                arm.open(.1,0);
                sleep(250);
                arm.close();
                sleep(250);
            }else{
                drive.followTrajectory(topW);
                arm.open();
                sleep(250);
                arm.close();
                sleep(250);
            }

            Trajectory collect1 = drive.trajectoryBuilder(drive.getPoseEstimate(), false)
                    .splineTo(new Vector2d(24, 65), 0)
                    .lineToLinearHeading(new Pose2d(49, 65, 0))
                    .addTemporalMarker(1.5, () -> {
                        intake.setPower(-1);
                        arm.moveArm(0, 1);
                        arm.open(.37,1);
                    })
                    .build();

            drive.followTrajectory(collect1);

            Trajectory deliver1 = drive.trajectoryBuilder(drive.getPoseEstimate(), true)
                    .splineTo(new Vector2d(24,65),Math.toRadians(180))
                    .splineTo(new Vector2d(-.89, 41.34), Math.toRadians(232.32))
                    .addTemporalMarker(0, () ->{
                        arm.close();
                    })
                    .addTemporalMarker(.25, () ->{
                        intake.setPower(1);
                    })
                    .addTemporalMarker(1, () ->{
                        arm.moveArm(-350, 1);
                    })
                    .addTemporalMarker(2, () -> {
                        intake.setPower(0);
                    })
                    .build();

            drive.followTrajectory(deliver1);

            arm.open();
            sleep(250);
            arm.close();

            Trajectory collect2 = drive.trajectoryBuilder(drive.getPoseEstimate(), false)
                    .splineTo(new Vector2d(24, 65), 0)
                    .splineToConstantHeading(new Vector2d(52,65), 0)
                    .addTemporalMarker(1.5, () -> {
                        arm.moveArm(0, .5);
                        intake.setPower(-1);
                        arm.open(.37,1);
                    })
                    .build();

            drive.followTrajectory(collect2);

            Trajectory backup2 = drive.trajectoryBuilder(drive.getPoseEstimate(), true)
                    .splineTo(new Vector2d(24,65),Math.toRadians(180))
                    .addTemporalMarker(0, () ->{
                        arm.close();
                    })
                    .addTemporalMarker(.25, () -> {
                        intake.setPower(1);
                    })
                    .build();

            drive.followTrajectory(backup2);

            Trajectory deliver2 = drive.trajectoryBuilder(drive.getPoseEstimate(), true)
                    .splineTo(new Vector2d(24,65),Math.toRadians(180))
                    .splineTo(new Vector2d(-.89, 41.34), Math.toRadians(232.32))
                    .addTemporalMarker(0, () ->{
                        arm.close();
                    })
                    .addTemporalMarker(.25, () -> {
                        intake.setPower(1);
                    })
                    .addTemporalMarker(1, () ->{
                        arm.moveArm(-350, 1);
                    })
                    .addTemporalMarker(2, () -> {
                        intake.setPower(0);
                    })
                    .build();

            drive.followTrajectory(deliver2);

            arm.open();
            sleep(250);
            arm.close();

            Trajectory park = drive.trajectoryBuilder(drive.getPoseEstimate())
                    .splineTo(new Vector2d(24, 65), 0)
                    .splineTo(new Vector2d(43.38, 65), 0)
                    .addTemporalMarker(2, () -> {
                        arm.moveArm(0, .5);
                        intake.setPower(-1);
                        arm.open();
                    })
                    .build();

            drive.followTrajectory(park);
        }else{
            Trajectory wheel = drive.trajectoryBuilder(drive.getPoseEstimate())
                    .lineToLinearHeading(new Pose2d(-62.38,57.25, Math.toRadians(180)))
                    .build();

            drive.followTrajectory(wheel);

            Trajectory strafe = drive.trajectoryBuilder(drive.getPoseEstimate())
                    .lineToLinearHeading(new Pose2d(-62.38, 63, Math.toRadians(180)))
                    .build();

            drive.followTrajectory(strafe);

            duckWheel.setVelocity(180, AngleUnit.DEGREES);
            sleep(3000);
            duckWheel.setVelocity(0, AngleUnit.DEGREES);

            Trajectory park = drive.trajectoryBuilder(drive.getPoseEstimate())
                    .lineToLinearHeading(new Pose2d(-64.38, 12, Math.toRadians(180)))
                    .build();

            drive.followTrajectory(park);

            Trajectory bottom = drive.trajectoryBuilder(drive.getPoseEstimate(), true)
                    .splineToLinearHeading(new Pose2d(-12,-10, Math.toRadians(270)), 0)
                    .addTemporalMarker(1, () -> {
                        arm.moveArm(-150, 1);
                    })
                    .build();

//        Trajectory followUp = drive.trajectoryBuilder(bottom.end(), true)
//                .lineToLinearHeading(new Pose2d(-12, -6, Math.toRadians(270)))
//                .build();

            Trajectory middle = drive.trajectoryBuilder(drive.getPoseEstimate(), true)
                    .splineToLinearHeading(new Pose2d(-12,-9.38, Math.toRadians(270)), 0)
                    .addTemporalMarker(.5, () -> {
                        arm.open(.25, 1);
                    })
                    .addTemporalMarker(1, () -> {
                        arm.moveArm(-250, 1);
                    })
                    .addTemporalMarker(2, () ->{
                        arm.open();
                    })
                    .build();

            Trajectory top = drive.trajectoryBuilder(drive.getPoseEstimate(), true)
                    .splineToLinearHeading(new Pose2d(-12,-3.38, Math.toRadians(270)), 0)
                    .addTemporalMarker(.5, () -> {
                        arm.moveArm(-350, 1);
                    })
                    .build();


            if(duckPos.equals(DuckDetectorPipelineBlue.DuckPos.LEFT)) {
                drive.followTrajectory(bottom);
//            drive.followTrajectory(followUp);
                arm.open();
                sleep(500);
                arm.close();
                sleep(250);
            }else if(duckPos.equals(DuckDetectorPipelineBlue.DuckPos.CENTER)){
                drive.followTrajectory(middle);
                sleep(500);
                arm.open(.1,0);
                sleep(500);
                arm.close();
                sleep(250);
            }else{
                drive.followTrajectory(top);
                arm.open();
                sleep(500);
                arm.close();
                sleep(250);
            }

            Trajectory backup = drive.trajectoryBuilder(drive.getPoseEstimate())
                    .lineToLinearHeading(new Pose2d(-12,-15, Math.toRadians(270)))
                    .build();

            drive.followTrajectory(backup);

            if(!warehousePark){
                Trajectory end = drive.trajectoryBuilder(drive.getPoseEstimate())
                        .lineToLinearHeading(new Pose2d(-64.38, 31, Math.toRadians(0)))
                        .addTemporalMarker(2, () -> {
                            arm.moveArm(0, .5);
                            arm.close();
                        })
                        .build();

                drive.followTrajectory(end);
            }
        }
    }
}
