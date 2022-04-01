package me.gaoliang.tinyrpc.protocol.serialization;

public class SerializationFactory {

    public static RpcSerialization getRpcSerialization(byte serializationType) {

        SerializationTypeEnum typeEnum = SerializationTypeEnum.findByType(serializationType);

        switch (typeEnum) {

            case HESSIAN:

                return new HessianSerialize();

            case JSON:

                return new JsonSerialization();

            default:

                throw new IllegalArgumentException("serialization type is illegal, " + serializationType);

        }

    }

}