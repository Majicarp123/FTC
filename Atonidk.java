import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;
import org.openftc.easyopencv.OpenCvPipeline;

@Autonomous
public class AutonomousCode extends LinearOpMode {
    DcMotor leftMotor, rightMotor, linearSlide;
    Servo servoClaw;
    String color;
    OpenCvCamera webcam;
    ColorDetectionPipeline colorDetectionPipeline;

    @Override
    public void runOpMode() {
        leftMotor = hardwareMap.get(DcMotor.class, "left_motor");
        rightMotor = hardwareMap.get(DcMotor.class, "right_motor
        linearSlide = hardwareMap.get(DcMotor.class, "linear_slide");
        servoClaw = hardwareMap.get(Servo.class, "servo_claw");

        leftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Logitech_webcam"), 640, 480);
        colorDetectionPipeline = new ColorDetectionPipeline();
        webcam.setPipeline(colorDetectionPipeline);
        webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);

        waitForStart();

        //scan cone for color
        color = colorDetectionPipeline.getColorString();
        telemetry.addData("Color: ", color);
        telemetry.update();

        //move to 5 inch pylon
        driveStraight(0.5, 1000);
        stop();

        if(color.equals("green")) {
            turnLeft(0.5, 500);
            driveStraight(0.5, 1000);
            stop();
        }
        else if(color.equals("black")) {
            //no need to move
        }
        else if(color.equals("purple")) {
            turnRight(0.5, 500);
            driveStraight(0.5, 1000);
            stop();
        }

        //lower linear slide to pick up 4 inch cone
        linearSlide.setPower(-0.5);
        sleep(1000);
        linearSlide.setPower(0);

        //open servo claw to grab cone
        servoClaw.setPosition(0.5);
        sleep(1000);

        //raise linear slide to place cone on 5 inch pylon
        linearSlide.setPower(0.5);
        sleep(1000);
        linearSlide.setPower(0);

        //close servo claw to release cone
        servoClaw.setPosition(0);
        sleep(1000);

        //move away from pylon
        driveStraight(-0.5, 1000);
        stop();
    }

    public void driveStraight(double power, int time) {
        leftMotor.setPower(power);
        rightMotor.setPower(power);
        sleep(time);
    }
    public void turnLeft(double power, int time) {
        leftMotor.setPower(-power);
        rightMotor.setPower(power);
        sleep(time);
    }

    public void turnRight(double power, int time) {
        leftMotor.setPower(power);
        rightMotor.setPower(-power);
        sleep(time);
    }

    public void stop() {
        leftMotor.setPower(0);
        rightMotor.setPower(0);
    }
}
