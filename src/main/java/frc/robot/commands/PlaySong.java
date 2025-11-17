package frc.robot.commands;

import java.util.function.Supplier;

import com.ctre.phoenix6.Orchestra;

import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class PlaySong extends InstantCommand {
    private final Orchestra orchestra;
    private final String path;
    private final SubsystemBase[] subsystems;

    public PlaySong(Orchestra orchestra, String path, SubsystemBase... orchestraSubsystems) {
        super();
        this.orchestra = orchestra;
        this.path = path;
        this.subsystems = orchestraSubsystems;

        addRequirements(orchestraSubsystems);
    }

    public PlaySong(Orchestra orchestra, Supplier<String> path, SubsystemBase... orchestraSubsystems) {
        super();
        this.orchestra = orchestra;
        this.path = path.get();
        this.subsystems = orchestraSubsystems;

        addRequirements(orchestraSubsystems);
    }

    @Override
    public void initialize() {
        for (SubsystemBase subsystem : subsystems) {
            Command requiring = CommandScheduler.getInstance().requiring(subsystem);
            if (requiring != null && subsystem.getDefaultCommand() != requiring) {
                this.cancel();
            }
        }
        
        var status = orchestra.loadMusic(path);
        if (status.isError()) {
            SmartDashboard.putStringArray("Status", new String[]{status.name(), path, Filesystem.getDeployDirectory().toString()});
            this.cancel();
        }

        orchestra.play();
        SmartDashboard.putString("Current Song", path);
    }
}
