/*
 *	Copyright 2013 HeroesGrave
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

package heroesgrave.paint.imageops;

import heroesgrave.paint.gui.Menu.CentredJDialog;
import heroesgrave.paint.main.Paint;
import heroesgrave.utils.misc.NumberDocumentFilter;

import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.text.AbstractDocument;

public class ResizeCanvas extends ImageOp
{
	public void operation()
	{
		final JDialog dialog = new CentredJDialog();
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 2));
		
		dialog.getContentPane().add(panel);
		
		dialog.setAlwaysOnTop(true);
		dialog.setAutoRequestFocus(true);
		
		dialog.setTitle("Resize Canvas");
		
		final JTextField width = new JTextField("" + Paint.main.gui.canvas.getImage().getWidth());
		final JTextField height = new JTextField("" + Paint.main.gui.canvas.getImage().getHeight());
		final JComboBox<String> filter = new JComboBox<String>();
		final DefaultComboBoxModel<String> filterModel = new DefaultComboBoxModel<String>(new String[]{"Nearest Neighbor", "Bilinear", "Bicubic"});
		
		((AbstractDocument) width.getDocument()).setDocumentFilter(new NumberDocumentFilter());
		((AbstractDocument) height.getDocument()).setDocumentFilter(new NumberDocumentFilter());
		filter.setModel(filterModel);
		
		width.setColumns(8);
		height.setColumns(8);
		
		JLabel wl = new JLabel("Width: ");
		wl.setHorizontalAlignment(SwingConstants.CENTER);
		JLabel hl = new JLabel("Height: ");
		hl.setHorizontalAlignment(SwingConstants.CENTER);
		
		JButton create = new JButton("Resize");
		JButton cancel = new JButton("Cancel");
		
		create.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dialog.dispose();
				resize(Integer.parseInt(width.getText()), Integer.parseInt(height.getText()), filterModel.getSelectedItem().toString());
			}
		});
		
		cancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dialog.dispose();
			}
		});
		
		panel.add(wl);
		panel.add(width);
		panel.add(hl);
		panel.add(height);
		panel.add(create);
		panel.add(cancel);
		
		dialog.pack();
		dialog.setResizable(false);
		dialog.setVisible(true);
	}
	
	public void resize(float w, float h, String filter)
	{
		BufferedImage old = Paint.main.gui.canvas.getImage();
		BufferedImage newImage = new BufferedImage((int) w, (int) h, BufferedImage.TYPE_INT_ARGB);
		
		for(int i = 0; i < newImage.getWidth(); i++)
		{
			for(int j = 0; j < newImage.getHeight(); j++)
			{
				newImage.setRGB(i, j, 0x00000000);
			}
		}
		
		Graphics2D g2d = (Graphics2D) newImage.getGraphics();
		g2d.drawImage(old, 0, 0, null);
		
		Paint.addChange(new ImageChange(newImage));
	}
}