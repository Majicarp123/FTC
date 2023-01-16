import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

public class AdvancedDrivetrain extends LinearOpMode {
    DcMotor leftMotor, rightMotor, linearSlide;

    @Override
    public void runOpMode() {
        leftMotor = hardwareMap.get(DcMotor.class, "left_motor");
        rightMotor = hardwareMap.get(DcMotor.class, "right_motor");
        linearSlide = hardwareMap.get(DcMotor.class, "linear_slide");

        leftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        while (opModeIsActive()) {
            double drive = gamepad1.left_stick_y;
            double turn = gamepad1.right_stick_x;
            double slide = gamepad2.left_stick_y;
            double leftPower, rightPower;
            double deadzone = 0.1;

            drive = Range.clip(drive, -1, 1);
            turn = Range.clip(turn, -1, 1);
            slide = Range.clip(slide, -1, 1);

            drive = (Math.abs(drive) > deadzone)? drive: 0;
            turn = (Math.abs(turn) > deadzone)? turn: 0;
            slide = (Math.abs(slide) > deadzone)? slide: 0;

            leftPower = drive + turn;
            rightPower = drive - turn;

            leftPower = Range.clip(leftPower, -1, 1);
            rightPower = Range.clip(rightPower, -1, 1);

            leftMotor.setPower(leftPower);
            rightMotor.setPower(rightPower);
            linearSlide.setPower(slide);

            telemetry.addData("Left Power", leftPower);
            telemetry.addData("Right Power", rightPower);
            telemetry.addData("Linear Slide Power", slide);
            telemetry.update();
        }
    }
}
