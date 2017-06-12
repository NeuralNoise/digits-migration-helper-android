/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.digits.auth.migration.internal;

import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;

import static android.content.ContentValues.TAG;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class ClearSessionContinuation implements Continuation<Void, Task<Void>> {

    private final StorageHelpers storageHelpers;

    public ClearSessionContinuation(StorageHelpers storageHelpers) {
        this.storageHelpers = storageHelpers;
    }

    @Override
    public Task<Void> then(@NonNull Task<Void> task) throws Exception {
        if (!task.isSuccessful()) {
            try {
                throw task.getException();
            } catch (FirebaseWebRequestException e) {
                if (e.getHttpStatusCode() == 400 || e.getHttpStatusCode() == 403) {
                    Log.d(TAG, "Digits session deemed invalid by server");
                    Log.d(TAG, "Clearing legacy session");
                    // Permanent errors should clear the persistence key.
                    storageHelpers.clearDigitsSession();
                }
                return task;
            }
        }
        storageHelpers.clearDigitsSession();
        return task;
    }
}