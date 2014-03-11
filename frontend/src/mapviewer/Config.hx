/*
 * Copyright 2014 Matthew Collins
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mapviewer;

@:native("MapViewerConfig")
extern class Config {
	
	public static var hostname : String;
	public static var port(get, never) : Int;
	
	@:extern inline static function get_port() : Int untyped {
		if (MapViewerConfig.port == "%SERVERPORT%") {
			// Running on a different server and hasn't changed the 
			// port number
			return 23333;
		}
		return Std.parseInt(MapViewerConfig.port);
	}
}