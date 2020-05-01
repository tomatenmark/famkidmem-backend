package de.mherrmann.famkidmem.backend.body;

public class ResponseBodyGet extends ResponseBody {

    private Object data;

    private ResponseBodyGet(){}

    public ResponseBodyGet(Object data, String subject){
        super("ok", "Successfully get: " + subject);
        this.data = data;
    }

    public ResponseBodyGet(Exception ex){
        super("error", ex.getMessage(), ex);
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
