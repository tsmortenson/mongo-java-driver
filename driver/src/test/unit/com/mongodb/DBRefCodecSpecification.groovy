/*
 * Copyright (c) 2008-2014 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb

import org.bson.BsonDocument
import org.bson.BsonDocumentReader
import org.bson.BsonDocumentWriter
import org.bson.BsonInt32
import org.bson.BsonString
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import org.bson.codecs.ValueCodecProvider
import org.bson.codecs.configuration.RootCodecRegistry
import spock.lang.Specification


class DBRefCodecSpecification extends Specification {
    def 'provider should return codec for DBRef class'() {
        expect:
        new DBRefCodecProvider().get(DBRef, new RootCodecRegistry([])) instanceof DBRefCodec
    }

    def 'provider should return null for non-DBRef class'() {
        expect:
        !new DBRefCodecProvider().get(Integer, new RootCodecRegistry([]))
    }

    def 'provider should be equal to another of the same class'() {
        expect:
        new DBRefCodecProvider() == new DBRefCodecProvider()
    }

    def 'provider should be not equal to any thing else'() {
        expect:
        new DBRefCodecProvider() != new ValueCodecProvider()
    }

    def 'codec should encode DBRef'() {
        given:
        def ref = new DBRef('foo', 1)
        def writer = new BsonDocumentWriter(new BsonDocument())

        when:
        writer.writeStartDocument()
        writer.writeName('ref')
        new DBRefCodec(new RootCodecRegistry([new ValueCodecProvider()])).encode(writer, ref, EncoderContext.builder().build())
        writer.writeEndDocument()

        then:
        writer.document == new BsonDocument('ref', new BsonDocument('$ref', new BsonString('foo')).append('$id', new BsonInt32(1)))
    }

    def 'codec should throw UnsupportedOperationException on decode'() {
        when:
        new DBRefCodec(new RootCodecRegistry([new ValueCodecProvider()])).decode(new BsonDocumentReader(new BsonDocument()),
                                                                                 DecoderContext.builder().build());

        then:
        thrown(UnsupportedOperationException)
    }
}