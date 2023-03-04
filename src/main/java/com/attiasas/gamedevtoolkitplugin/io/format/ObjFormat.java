package com.attiasas.gamedevtoolkitplugin.io.format;

import com.attiasas.gamedevtoolkitplugin.io.DataNode;
import com.attiasas.gamedevtoolkitplugin.parser.Tokenizer;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: Assaf, On 3/3/2023
 * @Description:
 **/
public class ObjFormat implements IFormat<DataNode> {

    private static final String VECTOR_ID = "v";
    private static final String TEXTURE_ID = "vt";
    private static final String NORMAL_ID = "vn";
    private static final String FACE_ID = "f";

    private static final String FACE_VEC_DELIMITER = "/";

    public static final String VERTICES = "vertices";
    public static final String VERTEX_POS_ATTRIBUTE = "position";
    public static final String VERTEX_TEXTURE_ATTRIBUTE = "texture";
    public static final String VERTEX_NORMAL_ATTRIBUTE = "normal";
    public static final String FACES = "faces";

    @Override
    public DataNode parse(String input) throws FormatException {
        List<Tokenizer.Token> tokens = new Tokenizer("\\s+","\n").tokenize(input);
        tokens = tokens.stream().filter(token -> isEndLine(token) || !token.token.matches("\\s+")).collect(Collectors.toList());
        int currentIndex = 0;
        DataNode root = new DataNode("ObjectMeshData");
        DataNode vertices = root.get(VERTICES);
        int verticesCount = 1;
        int textureCount = 1;
        int normalCount = 1;
        DataNode faces = root.get(FACES);
        int facesCount = 0;

        while (currentIndex < tokens.size()) {
            Tokenizer.Token token = tokens.get(currentIndex);
            switch (token.token.toLowerCase()) {
                case VECTOR_ID:
                    currentIndex += 1 + populateVectorDataFloats(vertices.get(indexStr(verticesCount++) + "." + VERTEX_POS_ATTRIBUTE), currentIndex + 1, tokens);
                    break;
                case TEXTURE_ID:
                    currentIndex += 1 + populateVectorDataFloats(vertices.get(indexStr(textureCount++) + "." + VERTEX_TEXTURE_ATTRIBUTE), currentIndex + 1, tokens);
                    break;
                case NORMAL_ID:
                    currentIndex += 1 + populateVectorDataFloats(vertices.get(indexStr(normalCount++) + "." + VERTEX_NORMAL_ATTRIBUTE), currentIndex + 1, tokens);
                    break;
                case FACE_ID:
                    currentIndex += 1 + populateFace(faces.get(indexStr(facesCount++)), vertices, currentIndex + 1, tokens);
                    break;
                default:
                    currentIndex++;
            }
        }

        return root;
    }

    private int populateFace(DataNode faceNode, DataNode vertices, int currentIndex, List<Tokenizer.Token> tokens) throws FormatException {
        Tokenizer.Token currentToken = tokens.get(currentIndex);
        try {
            while (currentToken.token.contains(FACE_VEC_DELIMITER)) {
                // Add new shape to face indicated by the index of the vertices (index start count from 1) separated by FACE_VEC_DELIMITER
                String[] shapeVector = currentToken.token.split(FACE_VEC_DELIMITER);
                DataNode shapeNode = faceNode.get(indexStr(faceNode.size()));
                for (int i = 0; i < shapeVector.length; i++) {
                    int index = Integer.parseInt(shapeVector[i]);
                    if (!vertices.has(indexStr(index))) {
                        throw new Exception("Vector index " + index + " is out of bounds (vectors count = " + vertices.size() + ")");
                    }
                    shapeNode.add(index);
                }
                if (currentIndex + faceNode.size() >= tokens.size()) {
                    // End of the tokens
                    break;
                }
                currentToken = tokens.get(currentIndex + faceNode.size());
            }

            if (faceNode.isEmpty()) {
                throw new Exception("Expected at least one shape definition after '" + FACE_ID + "' token");
            }

            return isEndLine(currentToken) ? (faceNode.size() + 1) : faceNode.size();
        } catch (Exception e) {
            throw new FormatException(currentToken, e);
        }
    }

    private int populateVectorDataFloats(DataNode vertexAttribNode, int currentIndex, List<Tokenizer.Token> tokens) throws FormatException {
        Tokenizer.Token currentToken = tokens.get(currentIndex);
        try {
            while (!isEndLine(currentToken)) {
                float data = Float.parseFloat(currentToken.token);
                vertexAttribNode.add(data);
                currentToken = tokens.get(currentIndex + vertexAttribNode.size());
            }
            int totalParsed = vertexAttribNode.size();
            if (totalParsed == 0) {
                throw new Exception("Expected at least one vertex '" + vertexAttribNode.indicator() + "' attribute data");
            }
            return totalParsed + 1;
        } catch (Exception e) {
            throw new FormatException(currentToken, e);
        }
    }

    private boolean isEndLine(Tokenizer.Token token) {
        return token.token.equals("\n");
    }

    private String indexStr(int index) {
        return "\"" + index + "\"";
    }

    @Override
    public String toString(DataNode data) throws FormatException {
        if (!data.has(VERTICES) || !data.has(FACES)) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();

        DataNode vertices = data.get(VERTICES);
        List<DataNode> textures = new ArrayList<>(vertices.size());
        List<DataNode> normals = new ArrayList<>(vertices.size());

        for (int i = 0; i < vertices.size(); i++) {
            DataNode vertex = vertices.get(i);
            if (!vertex.has(VERTEX_POS_ATTRIBUTE)) {
                throw new FormatException("Expected attribute '" + VERTEX_POS_ATTRIBUTE + "' to be in vertex object " + i);
            }
            stringBuilder.append(VECTOR_ID);
            vertexAttribToString(i, vertex.get(VERTEX_POS_ATTRIBUTE), stringBuilder);
            if (vertex.has(VERTEX_TEXTURE_ATTRIBUTE)) {
                textures.add(vertex.get(VERTEX_TEXTURE_ATTRIBUTE));
            }
            if (vertex.has(VERTEX_NORMAL_ATTRIBUTE)) {
                normals.add(vertex.get(VERTEX_NORMAL_ATTRIBUTE));
            }
        }
        for (int i = 0; i < textures.size(); i++) {
            stringBuilder.append(TEXTURE_ID);
            vertexAttribToString(i, textures.get(i), stringBuilder);
        }
        for (int i = 0; i < normals.size(); i++) {
            stringBuilder.append(NORMAL_ID);
            vertexAttribToString(i, normals.get(i), stringBuilder);
        }

        DataNode faces = data.get(FACES);
        for (int i = 0; i < faces.size(); i++) {
            faceToString(i, faces.get(i), stringBuilder);
        }
        return stringBuilder.toString();
    }

    private void faceToString(int faceIndex, DataNode face, StringBuilder builder) throws FormatException {
        builder.append(FACE_ID);
        if (face.isEmpty()) {
            throw new FormatException("Expected at least one shape in face object " + faceIndex);
        }
        for (int s = 0; s < face.size(); s++) {
            DataNode shape = face.get(s);
            if (shape.size() < 2) {
                throw new FormatException("Expected at least two vertex indices at shape '" + s + "' in face object " + faceIndex);
            }
            builder.append(" ").append(shape.get(0).toString());
            for (int i = 1; i < shape.size(); i++) {
                builder.append("/").append(shape.get(i).toString());
            }
        }
        builder.append("\n");
    }

    private void vertexAttribToString(int vertexIndex, DataNode attribute, StringBuilder builder) throws FormatException {
        if (attribute.isEmpty()) {
            throw new FormatException("Expected at least one value in '" + attribute.indicator() + "' attribute for vertex object " + vertexIndex);
        }
        for (int i = 0; i < attribute.size(); i++) {
            builder.append(" ").append(attribute.get(i).toString());
        }
        builder.append("\n");
    }

    public static void main(String[] args) {
        ObjFormat format = new ObjFormat();
        try {
            DataNode data = format.parse(Paths.get("src/test/resources/stall.obj"));
            System.out.println("Total Counts: [vertex=" + data.get(VERTICES).size() + ", faces=" + data.get(FACES).size() + "]");
            System.out.println("Data:\n" + data);
            System.out.println("toString:\n" + format.toString(data));
            Files.write(Paths.get("src/test/resources/myFileStall.obj"), format.toString(data).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
