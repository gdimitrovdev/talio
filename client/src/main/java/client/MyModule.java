/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package client;

import client.scenes.BoardCtrl;
import client.scenes.CardPopupCtrl;
import client.scenes.CreateBoardCtrl;
import client.scenes.HomeCtrl;
import client.scenes.JoinBoardCtrl;
import client.scenes.MainCtrlTalio;
import client.scenes.ServerConnectionCtrl;
import client.scenes.template.AddQuoteCtrl;
import client.scenes.template.MainCtrl;
import client.scenes.template.QuoteOverviewCtrl;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class MyModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(MainCtrl.class).in(Scopes.SINGLETON);
        binder.bind(AddQuoteCtrl.class).in(Scopes.SINGLETON);
        binder.bind(QuoteOverviewCtrl.class).in(Scopes.SINGLETON);
        binder.bind(MainCtrlTalio.class).in(Scopes.SINGLETON);
        binder.bind(HomeCtrl.class).in(Scopes.SINGLETON);
        binder.bind(JoinBoardCtrl.class).in(Scopes.SINGLETON);
        binder.bind(CreateBoardCtrl.class).in(Scopes.SINGLETON);
        binder.bind(ServerConnectionCtrl.class).in(Scopes.SINGLETON);
        binder.bind(CardPopupCtrl.class).in(Scopes.SINGLETON);
        binder.bind(BoardCtrl.class).in(Scopes.SINGLETON);
    }
}
