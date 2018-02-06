package io.github.jklingsporn.vertx.jooq.generate;

import io.github.jklingsporn.vertx.jooq.shared.internal.AbstractVertxDAO;
import org.jooq.impl.DAOImpl;
import org.jooq.util.JavaWriter;

import java.io.File;

/**
 * Replaces the DAO type in the generated class with the actual type. The type
 * is set externally by invoking the <code>#setDaoTypeReplacement</code> method
 * from the <code>VertxGenerator</code>.
 */
public class VertxJavaWriter extends JavaWriter {

    public static final String PLACEHOLDER_DAO_TYPE = "__DAO_TYPE__";
    private String daoTypeReplacement;

    public VertxJavaWriter(File file, String fullyQualifiedTypes) {
        super(file, fullyQualifiedTypes);
    }

    public VertxJavaWriter(File file, String fullyQualifiedTypes, String encoding) {
        super(file, fullyQualifiedTypes, encoding);
    }

    public void setDaoTypeReplacement(String daoTypeReplacement) {
        this.daoTypeReplacement = daoTypeReplacement;
    }

    @Override
    protected String beforeClose(String string) {
        return super.beforeClose(string);
    }

    private String replacePlaceholderDAOType(String string) {
        return string.replaceAll(PLACEHOLDER_DAO_TYPE, daoTypeReplacement);
    }

    private String replaceDAOImpl(String string){
        return string
                .replaceAll(DAOImpl.class.getName(),AbstractVertxDAO.class.getName()) //replace import
                .replaceAll(DAOImpl.class.getSimpleName(),AbstractVertxDAO.class.getSimpleName()); //replace extends
    }

    @Override
    public String ref(String clazzOrId, int keepSegments) {
        return super.ref(clazzOrId, keepSegments);
    }

    @Override
    public String ref(String clazz) {
        return super.ref(clazz);
    }
}
