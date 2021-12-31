package org.bleachhack.command.exception;

import net.minecraft.command.CommandException;

import java.io.Serial;

public class CmdSyntaxException extends CommandException {

	@Serial
	private static final long serialVersionUID = 7940377774005961331L;

	public CmdSyntaxException() {
        this("Invalid Syntax!");
    }

    public CmdSyntaxException(String message) {
        super(message);
    }

}
