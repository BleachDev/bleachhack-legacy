package org.bleachhack.command.exception;

import net.minecraft.command.CommandException;

public class CmdSyntaxException extends CommandException {

	private static final long serialVersionUID = 7940377774005961331L;

	public CmdSyntaxException() {
        this("Invalid Syntax!");
    }

    public CmdSyntaxException(String message) {
        super(message);
    }

}
