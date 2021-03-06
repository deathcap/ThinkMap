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

package uk.co.thinkofdeath.mapviewer.shared.block.blocks;

import uk.co.thinkofdeath.mapviewer.shared.IMapViewer;
import uk.co.thinkofdeath.mapviewer.shared.block.Block;
import uk.co.thinkofdeath.mapviewer.shared.block.BlockFactory;
import uk.co.thinkofdeath.mapviewer.shared.block.StateMap;
import uk.co.thinkofdeath.mapviewer.shared.block.states.BooleanState;
import uk.co.thinkofdeath.mapviewer.shared.block.states.IntegerState;

public class BlockLiquid extends BlockFactory {

    public final static String LEVEL = "level";
    public final static String FALLING = "falling";

    public BlockLiquid(IMapViewer iMapViewer) {
        super(iMapViewer);
        addState(LEVEL, new IntegerState(0, 7));
        addState(FALLING, new BooleanState());
    }

    @Override
    protected Block createBlock(StateMap states) {
        return new BlockImpl(states);
    }

    private class BlockImpl extends Block {
        public BlockImpl(StateMap states) {
            super(BlockLiquid.this, states);
        }

        @Override
        public int getLegacyData() {
            return this.<Integer>getState(LEVEL)
                    | (this.<Boolean>getState(FALLING) ? 8 : 0);
        }

        @Override
        public boolean shouldRenderAgainst(Block other) {
            return super.shouldRenderAgainst(other) && !(other instanceof BlockImpl);
        }
    }
}
