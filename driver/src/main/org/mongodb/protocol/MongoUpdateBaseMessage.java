/*
 * Copyright (c) 2008 - 2013 10gen, Inc. <http://10gen.com>
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

package org.mongodb.protocol;

import org.mongodb.Document;
import org.mongodb.io.ChannelAwareOutputBuffer;
import org.mongodb.operation.MongoUpdateBase;
import org.mongodb.serialization.Serializer;

public abstract class MongoUpdateBaseMessage extends MongoRequestMessage {
    private final Serializer<Document> baseSerializer;


    public MongoUpdateBaseMessage(final String collectionName, final OpCode opCode, final Serializer<Document> baseSerializer) {
        super(collectionName, opCode);
        this.baseSerializer = baseSerializer;
    }

    protected void writeBaseUpdate(final ChannelAwareOutputBuffer buffer) {
        buffer.writeInt(0); // reserved
        buffer.writeCString(getCollectionName());

        int flags = 0;
        if (getUpdateBase().isUpsert()) {
            flags |= 1;
        }
        if (getUpdateBase().isMulti()) {
            flags |= 2;
        }
        buffer.writeInt(flags);

        addDocument(getUpdateBase().getFilter(), baseSerializer, buffer);
    }

    protected abstract MongoUpdateBase getUpdateBase();

    public Serializer<Document> getBaseSerializer() {
        return baseSerializer;
    }
}