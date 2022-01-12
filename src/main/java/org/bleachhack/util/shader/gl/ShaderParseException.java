package org.bleachhack.util.shader.gl;

import java.io.FileNotFoundException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

public class ShaderParseException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private final List<FileTrace> traces;
    private final String message;
    
    public ShaderParseException(String message) {
        (this.traces = Lists.newArrayList()).add(new FileTrace());
        this.message = message;
    }
    
    public ShaderParseException(String message, Throwable cause) {
        super(cause);
        (this.traces = Lists.newArrayList()).add(new FileTrace());
        this.message = message;
    }
    
    public void addFaultyElement(String jsonKey) {
        this.traces.get(0).method_8099(jsonKey);
    }
    
    public void addFaultyFile(String path) {
        this.traces.get(0).field_8861 = path;
        this.traces.add(0, new FileTrace());
    }
    
    @Override
    public String getMessage() {
        return "Invalid " + this.traces.get(this.traces.size() - 1).toString() + ": " + this.message;
    }
    
    public static ShaderParseException wrap(Exception cause) {
        if (cause instanceof ShaderParseException) {
            return (ShaderParseException)cause;
        }
        String string2 = cause.getMessage();
        if (cause instanceof FileNotFoundException) {
            string2 = "File not found";
        }
        return new ShaderParseException(string2, cause);
    }
    
    private static class FileTrace {
        private String field_8861;
        private final List<String> field_8862;
        
        private FileTrace() {
            this.field_8861 = null;
            this.field_8862 = Lists.newArrayList();
        }
        
        private void method_8099(String string) {
            this.field_8862.add(0, string);
        }
        
        public String method_8101() {
            return StringUtils.join((Iterable<String>)this.field_8862, "->");
        }
        
        @Override
        public String toString() {
            if (this.field_8861 != null) {
                if (!this.field_8862.isEmpty()) {
                    return this.field_8861 + " " + this.method_8101();
                }
                return this.field_8861;
            }
            else {
                if (!this.field_8862.isEmpty()) {
                    return "(Unknown file) " + this.method_8101();
                }
                return "(Unknown file)";
            }
        }
    }
}
