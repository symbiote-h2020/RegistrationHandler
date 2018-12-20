/*
 *  Copyright 2018 Atos
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package eu.h2020.symbiote.rh.inforeader;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import eu.h2020.symbiote.cloud.model.internal.CloudResource;
import eu.h2020.symbiote.rh.PlatformInfoReader;

/**
 * Created by a141976 on 20/12/2016.
 */
/**! \class DummyPlatformInfoReader
 * \brief This class extends from \class PlatformInfoReader and implements a method  \a getResourcesToRegister that returns and empty list of \class CloudResource
 **/
@Component ("dummyPlatformInfoReader")
public class DummyPlatformInfoReader implements PlatformInfoReader {
    @Override
    public List<CloudResource> getResourcesToRegister() {
        return new ArrayList<CloudResource>();
    }
}
