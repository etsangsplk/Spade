/*
 *	Copyright 2013 HeroesGrave and other Paint.JAVA developers.
 *
 *	This file is part of Paint.JAVA
 *
 *	Paint.JAVA is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>
*/
package experimental.canvas;

import static experimental.canvas.Layer.Type.*;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JScrollPane;

import experimental.canvas.Layer.Type;
import experimental.canvas.context.RenderContext;

/**
 * NOTE: This class is in PROPOSAL stage
 * 
 * Class responsible for managing the work area.
 * It is a GUI component, it fills the role of the current GUIManager.scroll / GUIManager.panel
 * It is to be self contained in all of it's functionality.
 * This functionality includes:
 * 
 * Selection Management
 * Layer Management
 * 
 * 
 * 
 * Concept map:
 * 
 * Menus are to be a separate entity, not contained in the panel unlike GUIManager.panel.
 * JScrollPane functionality is still to be used for UI consistency and NIH syndrome.
 * However, all rendering (including zoom, centering, selection, etc.) is done manually.
 * Why? Letting Swing do that work is buggy as hell and unpleasant, both for us and users.
 * 
 * The Canvas area is rendered as a stack, with Z-ordering. This allows for fast and intuitive rendering and modification of complex "scenes."
 * A scene is the layers (including actual Layers) of draw information such as Changes, or the Selection.
 * 
 * @author BurntPizza
 *
 */
public class Canvas extends JScrollPane {

	private Panel panel;
	private double zoom;
	
	private Scene scene;
	
	
	// TODO: figure out appropriate params for methods, depends on Tool, Layer etc. implementations.
	
	public void createNewLayer() {
		
	}
	
	public void deleteLayer() {
		
	}
	
	public void moveLayer() {
		
	}
	
	// TODO: add more layer methods
	
	/**
	 * Adds the selection frame to the draw stack,
	 * intended to be called while user is making selection.
	 * 
	 * The resulting "selection trace" is volatile,
	 * it isn't an actual selection yet, only a preview.
	 */
	public void traceSelectionPreview() {
		// freeze scene to temp buffer
		// set selectionPreview field
		// draw only buffer and selectionPreview
	}
	
	/**
	 * Finalize selection trace and make actual selection.
	 */
	public void makeSelection() {
		// check if selectionPreview has been created
		// dispose temp buffer
		// make selection out of selctionPreview
		// add the selection to the stack
	}
	
	/**
	 * Turns off the selection frame
	 */
	public void deselect() {
		// remove selection from stack
	}
	
	/**
	 * Holds all the draw information in one place.
	 * 
	 * @author BurntPizza
	 */
	public static class Scene {
		
		List<Layer> layers; // still not sure on what these are to be exactly
		
		private boolean freezeActive;
		private BufferedImage freezeBuffer;

		/**
		 * 
		 * @param context
		 */
		public void render(RenderContext context) {
			if(freezeActive)
				renderFreezeBuffer(context);
			else
				renderLayers(FROZEN, context);
			renderLayers(UNFROZEN, context);
		}

		
		private void renderLayers(Type type, RenderContext context) {
			for(Layer layer : layers)
				if(type == BOTH || type == layer.getType())
					layer.draw(context);
		}


		private void renderFreezeBuffer(RenderContext context) {
			context.drawImage(freezeBuffer);
		}
	}
	
	/**
	 * This is here to override paint(Graphics g).
	 * I'm still not made up on whether or not it is needed or appropriate.
	 * Inherits from Canvas so as to be open to BufferStrategy.
	 * 
	 * @author BurntPizza
	 */
	private static class Panel extends java.awt.Canvas {
		
		private AffineTransform zoomTransform = AffineTransform.getScaleInstance(1, 1);
		private BufferStrategy buffer;
		private Scene scene;
		
		private void render(Graphics2D g) {
			//RenderContext context = genContext(g); // figure this out
			//scene.render(context);
			//context.flush();
		}
		
		
		@Override
		public void paint(Graphics g) {
			if(buffer == null)
				initBufferStrategy();
			
			Graphics2D bufferGraphics = null;
			
			do {
			    try{
			    	bufferGraphics = (Graphics2D) buffer.getDrawGraphics();
			    	bufferGraphics.setTransform(zoomTransform);
			        render(bufferGraphics);
			    } finally {
			    	if (bufferGraphics != null)
			    		bufferGraphics.dispose();
			    }
			    buffer.show();
			} while (buffer.contentsLost());
		}
		
		public void initBufferStrategy() {
			createBufferStrategy(2);
			buffer = getBufferStrategy();
		}
		
		public void dispose() {
			buffer.dispose();
		}
		
		public void setZoom(double z) {
			zoomTransform.setToScale(z, z);
		}
		
	}
}